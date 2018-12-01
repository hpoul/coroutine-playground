import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.10"
    application
}

application {
    mainClassName = "design.codeux.coroutine.playground.CoroutineExampleKt"
}

group = "design.codeux"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    // logging
    compile("io.github.microutils:kotlin-logging:1.4.9")
    compile("org.slf4j:jul-to-slf4j:1.7.25")
    compile("ch.qos.logback:logback-classic:1.2.1")
    compile("net.logstash.logback:logstash-logback-encoder:5.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}