plugins {
    id("dev.ligature.slonky.kotlin-library-conventions")
}

dependencies {
    api(project(":ligature"))
    api(project(":lig"))
    implementation(project(":wander-grammar"))
    implementation("dev.ligature:rakkoon:0.1.0-SNAPSHOT")
    testImplementation(project(":ligature-in-memory"))
    testImplementation("io.kotest:kotest-runner-junit5:4.6.0")
}
