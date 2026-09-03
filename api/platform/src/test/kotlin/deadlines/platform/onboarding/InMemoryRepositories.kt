package deadlines.platform.onboarding

import deadlines.platform.access.domain.GrantedPermission
import deadlines.platform.access.domain.Permission
import deadlines.platform.access.domain.PermissionRepository
import deadlines.platform.access.domain.Role
import deadlines.platform.access.domain.RoleRepository
import deadlines.platform.accounts.domain.Slug
import deadlines.platform.accounts.domain.Tenant
import deadlines.platform.accounts.domain.TenantRepository
import deadlines.platform.accounts.domain.TenantUser
import deadlines.platform.accounts.domain.TenantUserRepository
import deadlines.platform.billing.domain.Subscription
import deadlines.platform.billing.domain.SubscriptionRepository
import deadlines.platform.identity.domain.Email
import deadlines.platform.identity.domain.User
import deadlines.platform.identity.domain.UserRepository
import deadlines.platform.persistence.application.TransactionRunner
import java.util.UUID

class InMemoryTenantRepository(var failOnCreate: Boolean = false) : TenantRepository {
    val saved = mutableListOf<Tenant>()

    override suspend fun findById(id: UUID) = saved.firstOrNull { it.id == id }

    override suspend fun existsBySlug(slug: Slug) = saved.any { it.slug.value == slug.value }

    override suspend fun create(tenant: Tenant): Tenant {
        if (failOnCreate) error("tenant creation failed")
        saved += tenant
        return tenant
    }
}

class InMemoryTenantUserRepository : TenantUserRepository {
    val saved = mutableListOf<TenantUser>()

    override suspend fun find(tenantId: UUID, userId: UUID) =
        saved.firstOrNull { it.tenantId == tenantId && it.userId == userId }

    override suspend fun create(tenantUser: TenantUser): TenantUser {
        saved += tenantUser
        return tenantUser
    }
}

class InMemoryUserRepository : UserRepository {
    val saved = mutableListOf<User>()

    override suspend fun findByEmail(email: Email) = saved.firstOrNull { it.email.value == email.value }

    override suspend fun findById(id: UUID) = saved.firstOrNull { it.id == id }

    override suspend fun create(user: User): User {
        saved += user
        return user
    }
}

class InMemoryRoleRepository : RoleRepository {
    val saved = mutableListOf<Role>()
    val grants = mutableMapOf<UUID, List<GrantedPermission>>()
    val assignments = mutableListOf<Pair<UUID, UUID>>()

    override suspend fun findByKey(tenantId: UUID, key: String) =
        saved.firstOrNull { it.tenantId == tenantId && it.key == key }

    override suspend fun create(role: Role): Role {
        saved += role
        return role
    }

    override suspend fun grant(roleId: UUID, permissions: List<GrantedPermission>) {
        grants[roleId] = permissions
    }

    override suspend fun assign(tenantUserId: UUID, roleId: UUID) {
        assignments += tenantUserId to roleId
    }
}

class InMemoryPermissionRepository(private val catalogue: List<Permission>) : PermissionRepository {
    override suspend fun all() = catalogue
}

class InMemorySubscriptionRepository : SubscriptionRepository {
    val saved = mutableListOf<Subscription>()

    override suspend fun findByTenant(tenantId: UUID) = saved.firstOrNull { it.tenantId == tenantId }

    override suspend fun create(subscription: Subscription): Subscription {
        saved += subscription
        return subscription
    }
}

class DirectTransactionRunner : TransactionRunner {
    override suspend fun <T> transaction(block: suspend () -> T): T = block()
}
