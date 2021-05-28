plugins {
    // Apply the common convention plugin for shared build configuration between library and application projects.
    id("dev.ligature.slonky.kotlin-common-conventions")

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

dependencies {
    implementation("io.vertx:vertx-web:4.0.2")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:4.0.2")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation(project(":lig"))
    implementation(project(":ligature-in-memory"))
    testImplementation("io.vertx:vertx-web-client:4.0.2")
    testImplementation("io.kotest:kotest-runner-junit5:4.6.0")
    testImplementation("io.kotest:kotest-assertions-core:4.6.0")
}

application {
    mainClass.set("dev.ligature.slonky.AppKt")
}
