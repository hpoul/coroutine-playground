import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM
    id("org.jetbrains.kotlin.jvm").version("1.3.10")
    application
}

application {
    mainClassName = "design.codeux.coroutine.playground.CoroutineExampleKt"
}

group = "design.codeux"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    // Use the Kotlin JDK 8 standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.0.1")

    // logging
    implementation("io.github.microutils:kotlin-logging:1.4.9")
    implementation("org.slf4j:jul-to-slf4j:1.7.25")
    implementation("ch.qos.logback:logback-classic:1.2.1")
    implementation("net.logstash.logback:logstash-logback-encoder:5.2")
}
//
//tasks.withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = "1.8"
//}

val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlin.Experimental"
        )
    }
}
