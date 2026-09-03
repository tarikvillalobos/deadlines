dependencies {
    api(project(":core"))
    api(project(":contracts"))
    implementation(libs.bundles.persistence)
    testImplementation(libs.bundles.kotest)
}
