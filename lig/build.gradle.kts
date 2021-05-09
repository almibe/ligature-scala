plugins {
    id("dev.ligature.slonky.kotlin-library-conventions")
}

dependencies {
    api(project(":ligature"))
    testImplementation("io.kotest:kotest-runner-junit5:4.4.1")
}
