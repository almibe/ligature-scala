plugins {
    antlr
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.9.2")
}

tasks.generateGrammarSource {
    outputDirectory = File("${project.buildDir}/generated-src/antlr/main/dev/ligature/wander/parser")
    arguments = arguments + listOf("-visitor")
}
