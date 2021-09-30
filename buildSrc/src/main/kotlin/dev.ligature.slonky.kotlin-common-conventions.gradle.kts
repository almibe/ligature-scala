plugins {
    id("org.jetbrains.kotlin.jvm")
    jacoco
    id("maven-publish")
}

repositories {
    mavenLocal()
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xallow-result-return-type"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        jvmTarget = "1.8"
    }
}

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}
