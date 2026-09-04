CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE tenants (
    id         uuid PRIMARY KEY,
    name       text        NOT NULL,
    slug       text        NOT NULL UNIQUE,
    status     text        NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE users (
    id            uuid PRIMARY KEY,
    email         citext      NOT NULL UNIQUE,
    password_hash text        NOT NULL,
    name          text        NOT NULL,
    created_at    timestamptz NOT NULL DEFAULT now(),
    updated_at    timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE tenant_users (
    id         uuid PRIMARY KEY,
    tenant_id  uuid        NOT NULL REFERENCES tenants (id) ON DELETE CASCADE,
    user_id    uuid        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status     text        NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, user_id)
);

CREATE INDEX tenant_users_user_id_idx ON tenant_users (user_id);

CREATE TABLE permissions (
    key         text PRIMARY KEY,
    module      text NOT NULL,
    description text NOT NULL
);

CREATE TABLE roles (
    id         uuid PRIMARY KEY,
    tenant_id  uuid        NOT NULL REFERENCES tenants (id) ON DELETE CASCADE,
    key        text        NOT NULL,
    name       text        NOT NULL,
    is_system  boolean     NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, key)
);

CREATE TABLE role_permissions (
    role_id       uuid NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    permission_key text NOT NULL REFERENCES permissions (key) ON DELETE CASCADE,
    scope         text NOT NULL,
    PRIMARY KEY (role_id, permission_key)
);

CREATE TABLE user_roles (
    tenant_user_id uuid NOT NULL REFERENCES tenant_users (id) ON DELETE CASCADE,
    role_id        uuid NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (tenant_user_id, role_id)
);

CREATE TABLE subscriptions (
    id             uuid PRIMARY KEY,
    tenant_id      uuid        NOT NULL UNIQUE REFERENCES tenants (id) ON DELETE CASCADE,
    status         text        NOT NULL,
    trial_ends_at  timestamptz,
    created_at     timestamptz NOT NULL DEFAULT now(),
    updated_at     timestamptz NOT NULL DEFAULT now()
);

INSERT INTO permissions (key, module, description) VALUES
    ('platform.users.read',       'platform', 'List the people who belong to the account'),
    ('platform.users.invite',     'platform', 'Invite new people to the account'),
    ('platform.users.update',     'platform', 'Change a member name or roles'),
    ('platform.users.deactivate', 'platform', 'Deactivate or reactivate a member'),
    ('platform.roles.read',       'platform', 'List roles and their permissions'),
    ('platform.roles.update',     'platform', 'Create, change or delete roles'),
    ('platform.billing.read',     'platform', 'See the subscription, invoices and usage'),
    ('platform.billing.manage',   'platform', 'Change the subscription and contracted modules');
