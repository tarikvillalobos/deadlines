plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

application {
    mainClass = "deadlines.app.ApplicationKt"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":contracts"))
    implementation(project(":platform"))
    implementation(project(":domains"))
    implementation(libs.bundles.ktor.server)
    implementation(libs.logback.classic)
    implementation(libs.bundles.persistence)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.konsist)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.koin.test)
}
