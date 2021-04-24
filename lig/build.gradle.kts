plugins {
    id("dev.ligature.slonky.kotlin-library-conventions")
}

dependencies {
    api(project(":ligature"))
    api("dev.ligature:rakkoon:0.1.0-SNAPSHOT")
    testImplementation("io.kotest:kotest-runner-junit5:4.4.1")
}