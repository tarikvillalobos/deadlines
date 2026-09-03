# Deadlines

Multi-tenant SaaS combining CRM and ERP: commercial pipeline, orders, inventory, purchasing, production, logistics and finance.

## Layout

```
deadlines/
├── api/    Kotlin + Ktor backend (Gradle multi-module)
├── web/    Next.js frontend
└── docs/   architecture and design notes
```

Each part has its own README with setup instructions.

## Deployment

- `web/` is deployed to Vercel with the root directory set to `web`.
- `api/` and PostgreSQL run as Docker containers (see `docker-compose.yml`).
