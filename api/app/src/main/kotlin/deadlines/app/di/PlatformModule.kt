package deadlines.app.di

import deadlines.platform.access.domain.PermissionRepository
import deadlines.platform.access.domain.RoleRepository
import deadlines.platform.access.infrastructure.ExposedPermissionRepository
import deadlines.platform.access.infrastructure.ExposedRoleRepository
import deadlines.platform.accounts.domain.TenantRepository
import deadlines.platform.accounts.domain.TenantUserRepository
import deadlines.platform.accounts.infrastructure.ExposedTenantRepository
import deadlines.platform.accounts.infrastructure.ExposedTenantUserRepository
import deadlines.platform.billing.domain.SubscriptionRepository
import deadlines.platform.billing.infrastructure.ExposedSubscriptionRepository
import deadlines.platform.identity.application.PasswordHasher
import deadlines.platform.identity.domain.UserRepository
import deadlines.platform.identity.infrastructure.Argon2PasswordHasher
import deadlines.platform.identity.infrastructure.ExposedUserRepository
import deadlines.platform.onboarding.application.SignUp
import org.koin.dsl.module

val platformModule =
    module {
        single<TenantRepository> { ExposedTenantRepository() }
        single<TenantUserRepository> { ExposedTenantUserRepository() }
        single<UserRepository> { ExposedUserRepository() }
        single<RoleRepository> { ExposedRoleRepository() }
        single<PermissionRepository> { ExposedPermissionRepository() }
        single<SubscriptionRepository> { ExposedSubscriptionRepository() }
        single<PasswordHasher> { Argon2PasswordHasher() }
        single { SignUp(get(), get(), get(), get(), get(), get(), get(), get()) }
    }
