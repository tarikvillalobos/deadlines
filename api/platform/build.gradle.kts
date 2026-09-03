dependencies {
    api(project(":core"))
    api(project(":contracts"))
    api(libs.kotlinx.datetime)
    implementation(libs.bundles.persistence)
    implementation(libs.password4j)
    testImplementation(libs.bundles.kotest)
}
