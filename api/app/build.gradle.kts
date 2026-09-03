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
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.konsist)
}
