## Arquitetura Completa — Deadlines

### Stack

```
Linguagem      → Kotlin
API            → Ktor (ktor-server-auth, JWT curto + refresh rotativo)
DI             → Koin
Banco          → PostgreSQL (único, multi-tenant por tenant_id + RLS)
Acesso a dados → Exposed (jOOQ depois, só em relatórios complexos)
Migrations     → Flyway
Serialização   → kotlinx.serialization
Senhas         → Argon2
Jobs/filas     → tabela jobs no Postgres (FOR UPDATE SKIP LOCKED) + coroutines
                 (Redis/Rabbit só quando escalar — trocando implementação, não código)
Cache          → Redis (Lettuce) quando precisar; começa sem
Storage        → S3-compatível
Testes         → Kotest + Testcontainers
Gateway pgto   → interface PaymentGateway; começa com ManualGateway,
                 depois Asaas/Pagar.me (boleto+Pix) ou Stripe
```

### Estrutura de módulos (Gradle multi-módulo)

```
deadlines/
├── app/            → bootstrap Ktor: rotas, DI, plugins, config
├── core/           → TenantContext, RequestContext (tenant+user+permissões),
│                     DomainEvent + EventBus (in-process → fila depois),
│                     EntitlementService, erros, paginação
│
├── platform/       ← chassi do SaaS (não sabe nada do negócio)
│   ├── identity        → login, refresh, MFA, sessões
│   ├── accounts        → empresa/conta, convites
│   ├── access          → roles, permissões, teams (RBAC + scopes own/team/all)
│   ├── billing         → módulos contratáveis, assinatura, assentos, webhooks
│   ├── workflow        → engine de boards/stages (kanban + timeline)
│   ├── automation      → gatilhos entre módulos
│   ├── notifications   → sino, e-mail, push
│   ├── audit           → trilha de auditoria
│   ├── files           → anexos (S3)
│   └── assistant       → [futuro] AI: tools dos domínios + RequestContext
│
└── domains/        ← o negócio (importa platform; NUNCA importa outro domain)
    ├── commercial      → leads, pipeline, propostas, clientes, campanhas
    ├── orders          → pedido de venda (espinha dorsal; grátis com qualquer módulo)
    ├── inventory       → catálogo completo, estoques, movimentações
    ├── purchasing      → fornecedores, cotações, pedidos de compra
    ├── production      → OPs, BOM, cronograma
    ├── logistics       → romaneio, rotas, rastreio, devoluções
    └── finance         → a pagar/receber, faturamento, fluxo de caixa
```

**Regras de dependência:**
```
domains  → platform          ✅
platform → domains           ❌ nunca
domain   → outro domain      ❌ só via DomainEvent
AI/endpoints → services      ✅ única porta de acesso a dados (nunca SQL no handler)
```

### Tabelas

**platform/identity + accounts + access**
```
tenants             (id, name, slug, status)
users               (id, email, password_hash, name)
tenant_users        (tenant_id, user_id, status)          ← desativado não conta assento
roles               (id, tenant_id, name)
permissions         (id, key)                             ← seed: "commercial.lead.edit"...
role_permissions    (role_id, permission_id, scope)       ← own | team | all
user_roles          (tenant_user_id, role_id)
teams               (id, tenant_id, name)
team_members        (team_id, tenant_user_id)
invitations         (id, tenant_id, email, role_id, token, expires_at)
refresh_tokens      (id, user_id, token_hash, expires_at, revoked_at)
```

**platform/billing** (precificação modular + assentos + uso)
```
modules              (id, key, name, price_cents, requires_json, active)
subscriptions        (id, tenant_id, status, trial_ends_at,
                      current_period_start, current_period_end,
                      included_seats, extra_seat_price_cents,
                      gateway, gateway_subscription_id, canceled_at)
subscription_modules (subscription_id, module_id, price_cents_snapshot,
                      added_at, removed_at)               ← soft delete, preço congelado
subscription_events  (id, subscription_id, type, payload_json, created_at)
usage_records        (id, tenant_id, metric, quantity, ref_type, ref_id, recorded_at)
                     ← ai_credits futuramente; leads/mês, storage etc desde já
```
Status: `trialing | active | past_due | canceled | expired`.
Cobrança = módulos ativos + `max(0, usuários_ativos − included_seats) × extra_seat`.
Dependências: `production→inventory`, `logistics→orders`; `commercial`, `inventory`, `finance` standalone.
Inadimplente/cancelado: **lê e exporta, não escreve**. Módulo cancelado: dados ficam read-only.

**platform/workflow** (engine genérica)
```
boards              (id, tenant_id, module, name)
stages              (id, board_id, name, position, color, is_final)
```
Cards não têm tabela própria: cada entidade carrega `stage_id + position`. Kanban e timeline são visualizações da mesma linha.

**platform/automation + notifications + audit + files**
```
automations         (id, tenant_id, trigger_json, conditions_json, actions_json, active)
automation_runs     (id, automation_id, entity_id, status, error, ran_at)
notifications       (id, tenant_id, user_id, type, payload_json, read_at)
audit_logs          (id, tenant_id, user_id, action, entity_type, entity_id,
                     before_json, after_json, ip, created_at)
attachments         (id, tenant_id, entity_type, entity_id, url, filename, size)
custom_fields       (id, tenant_id, module, key, label, field_type, options_json)
custom_field_values (id, custom_field_id, entity_id, value_json)
jobs                (id, type, payload_json, status, run_at, attempts, locked_at)
```

**domains/commercial**
```
clients             (id, tenant_id, name, document, email, phone, address_json)
leads               (id, tenant_id, client_id?, stage_id, position, owner_id, source, value)
proposals           (id, tenant_id, lead_id, number, status, valid_until, total)
proposal_items      (id, proposal_id, product_id, qty, unit_price, discount)
campaigns           (id, tenant_id, name, channel, budget, starts_at, ends_at)
activities          (id, tenant_id, entity_type, entity_id, type, notes, due_at, done_at)
```

**domains/orders**
```
orders              (id, tenant_id, client_id, proposal_id?, number, status, total)
order_items         (id, order_id, product_id, qty, unit_price)
```

**domains/inventory** (catálogo nasce simples na fase 1, engorda na fase 2)
```
products            (id, tenant_id, sku, name, type, price, cost)   ← raw|finished|resale
product_variants    (id, product_id, attrs_json, sku)
warehouses          (id, tenant_id, name, kind)                     ← raw_material|finished_goods
stock_items         (id, warehouse_id, product_id, qty, min_qty)
stock_movements     (id, tenant_id, product_id, warehouse_id, qty, type, ref_type, ref_id)
                    ← in|out|transfer|adjustment; razão imutável
```

**domains/purchasing**
```
suppliers            (id, tenant_id, name, document, contact_json)
quotations           (id, tenant_id, supplier_id, status, stage_id)
quotation_items      (id, quotation_id, product_id, qty, unit_price)
purchase_orders      (id, tenant_id, supplier_id, status, stage_id, total, expected_at)
purchase_order_items (id, purchase_order_id, product_id, qty, unit_price)
```

**domains/production**
```
boms                (id, tenant_id, product_id)
bom_items           (id, bom_id, component_product_id, qty)
production_orders   (id, tenant_id, order_id?, product_id, qty, stage_id,
                     planned_start, planned_end, started_at, finished_at)
```

**domains/logistics**
```
shipments           (id, tenant_id, order_id, stage_id, tracking_code, carrier)
shipment_items      (id, shipment_id, order_item_id, qty)
routes              (id, tenant_id, driver_id?, date, status)
route_stops         (id, route_id, shipment_id, position, delivered_at, proof_url)
returns             (id, tenant_id, order_id, reason, status, stage_id)
```

**domains/finance**
```
invoices            (id, tenant_id, order_id?, number, type, status, total, issued_at, nfe_key)
invoice_items       (id, invoice_id, product_id, qty, unit_price, tax_json)
receivables         (id, tenant_id, client_id, invoice_id?, due_date, amount, paid_at, stage_id)
payables            (id, tenant_id, supplier_id?, purchase_order_id?, due_date,
                     amount, paid_at, stage_id, category_id)
finance_categories  (id, tenant_id, name, kind)                     ← income|expense
payments            (id, tenant_id, ref_type, ref_id, method, amount, paid_at)
```

### Endpoints (`/api/v1`, tenant vem do token, guard de permissão + entitlement em tudo)

```
AUTH        POST /auth/login | /refresh | /logout | /forgot-password | /reset-password
IAM         CRUD /users /roles /teams · GET /permissions · PUT /roles/:id/permissions
            POST /invitations
BILLING     GET /billing/modules · POST /billing/subscribe · POST /billing/modules/:key
            DELETE /billing/modules/:key · GET /billing/usage
            POST /webhooks/billing/{gateway}          ← idempotente, valida assinatura
WORKFLOW    GET /boards?module= · CRUD /boards/:id/stages
            PATCH /cards/:entityType/:id/move         ← {stage_id, position}; dispara eventos
COMMERCIAL  CRUD /leads /clients /proposals /campaigns /activities
            POST /leads/:id/convert
ORDERS      CRUD /orders · POST /orders/:id/fulfill
INVENTORY   CRUD /products · GET /stock?warehouse_id · POST /stock/movements
PURCHASING  CRUD /suppliers /quotations /purchase-orders
PRODUCTION  CRUD /production-orders /boms · GET /production-orders/schedule?from&to
LOGISTICS   CRUD /shipments /routes /returns · POST /shipments/:id/deliver
FINANCE     CRUD /invoices /receivables /payables · POST /receivables/:id/pay
            GET /finance/cashflow?from&to
PLATAFORMA  CRUD /automations /custom-fields · GET /notifications · GET /audit-logs
            POST /attachments
```

### Eventos de domínio (a cola entre módulos)

```
LeadCreated, LeadStageChanged, ProposalAccepted
OrderConfirmed          → inventory baixa/reserva; purchasing sugere compra;
                          production cria rascunho de OP
ProductionOrderFinished → inventory entrada de acabado; logistics libera separação
ShipmentDelivered       → finance gera receivable
PurchaseOrderConfirmed  → finance gera payable
TrialExpired, SeatLimitReached, ModuleActivated...
```
Módulo não contratado = eventos dele nunca publicados = integrações se ligam/desligam sozinhas, sem `if` espalhado.

### Fases de construção

```
FASE 0 — Fundação
  core + identity + accounts + access + workflow + audit + billing (ManualGateway)
  + jobs + usage_records + EventBus. tenant_id em TUDO desde a migration 1.

FASE 1 — Comercial (primeiro SKU vendável)
  commercial + notifications + files + catálogo simples de produtos
  + gateway de pagamento real

FASE 2 — Estoque (SKU: inventory)
  inventory completo (SKU, custo, depósitos, movimentações)

FASE 3 — Compras + Expedição (SKUs: purchasing, logistics)
  orders formalizado + purchasing + logistics + automações entre módulos

FASE 4 — Produção (SKU: production)
  production (OP, BOM, cronograma) escutando eventos já existentes

FASE 5 — Financeiro (SKU: finance)
  finance fecha o ciclo lead-to-cash + NF-e (integração fiscal)

FASE 6 — AI (SKU: assistant, cobrança medida via usage_records)
  platform/assistant: tools por domínio, mesmo RequestContext/permissões do usuário
```

### Princípios inegociáveis (o que garante que "começar pequeno" funcione)

1. `tenant_id` em toda tabela + índice `(tenant_id, ...)` desde o dia 1
2. Toda lógica de dados nos **services** — endpoints e AI são só consumidores
3. Eventos publicados desde o dia 1, mesmo sem consumidores
4. Workflow genérico — nenhum board hardcoded
5. Catálogo de permissões versionado no formato final (`module.entity.action` + scope)
6. Nunca sequestrar dados: inadimplente lê, cancelado exporta
7. App stateless — escala horizontal trocando nada

Quer que eu gere agora o esqueleto do projeto (Gradle multi-módulo + Ktor + Flyway com a fase 0)?