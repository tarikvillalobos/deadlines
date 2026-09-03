dependencies {
    api(project(":core"))
    api(project(":contracts"))
    api(project(":platform"))
    testImplementation(libs.bundles.kotest)
}
