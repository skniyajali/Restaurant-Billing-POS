plugins {
    id("popos.android.feature")
    id("popos.android.library.compose")
    id("popos.android.library.jacoco")
}

android {
    namespace = "com.niyaj.feature.reports"
    ksp {
        arg("compose-destinations.moduleName", "reports")
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.useComposableVisibility", "true")
    }
}

dependencies {
    implementation(project(":feature:chart"))
    implementation(libs.accompanist.permissions)
    implementation(libs.dialog.core)
    implementation(libs.dialog.datetime)
    implementation(libs.pos.printer)


    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)
}