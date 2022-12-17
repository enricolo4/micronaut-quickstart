val javaVersion: String by project
val kotestRunnerVersion: String by project
val micronautVersion: String by project
val mockkVersion: String by project
val reactorKotlinExtensionVersion: String by project

buildscript {
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
    val kotlinVersion = "1.7.21"

    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jcenter.bintray.com")
        maven("https://packages.confluent.io/maven/")
        maven("https://jitpack.io")
    }

    dependencies {
        classpath(kotlin("gradle-plugin:$kotlinVersion"))
    }
}

plugins {
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
    val kotlinVersion = "1.7.21"

    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("de.undercouch.download") version "5.3.0"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "kotlin-jpa")
    apply(plugin = "kotlin-allopen")
    apply(plugin = "de.undercouch.download")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jcenter.bintray.com")
        maven("https://packages.confluent.io/maven/")
        maven("https://jitpack.io")
    }

    dependencies {
        kapt("io.micronaut:micronaut-bom:$micronautVersion")
        kapt("io.micronaut:micronaut-graal")
        kapt("io.micronaut:micronaut-validation")
        kapt("io.micronaut:micronaut-inject-java")
    }
}

subprojects {
    group = "com.quickstart"
    version = "1.0.0"

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }

    sourceSets.main {
        java.srcDir("src/main/kotlin")
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

    dependencies {
        kapt("io.micronaut:micronaut-bom:$micronautVersion")
        kapt("io.micronaut:micronaut-graal")
        kapt("io.micronaut:micronaut-validation")
        kapt("io.micronaut:micronaut-inject-java")

        implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))

        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorKotlinExtensionVersion")
        implementation("jakarta.annotation:jakarta.annotation-api")
        implementation("jakarta.transaction:jakarta.transaction-api:2.0.1")

        implementation("io.micronaut.reactor:micronaut-reactor")
        implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
        implementation("io.micronaut:micronaut-inject")
        implementation("io.micronaut:micronaut-validation")
        implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")

        implementation("javax.annotation:javax.annotation-api")

        runtimeOnly("ch.qos.logback:logback-classic")
        runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

        kaptTest("io.micronaut:micronaut-bom:$micronautVersion")
        kaptTest("io.micronaut:micronaut-graal")
        kaptTest("io.micronaut:micronaut-validation")
        kaptTest("io.micronaut:micronaut-inject-java")

        testImplementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))

        testImplementation("io.micronaut.test:micronaut-test-kotest")
        testImplementation("io.mockk:mockk:$mockkVersion")
        testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestRunnerVersion")
        testImplementation("javax.annotation:javax.annotation-api")
    }

    java {
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = javaVersion
            }
        }
        compileTestKotlin {
            kotlinOptions {
                jvmTarget = javaVersion
            }
        }
        test {
            useJUnitPlatform()
        }
    }
}

tasks {
    build {
        setDependsOn(subprojects.map { it.getTasksByName("build", false) })
    }

    val temporaryFolder = file("platform/tmp")

    register("downloadNewrelic", de.undercouch.gradle.tasks.download.Download::class) {
        mkdir(temporaryFolder)
        src("https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip")
        dest(temporaryFolder)
    }

    register("installNewRelic", Copy::class) {
        val zipFile = file("$temporaryFolder/newrelic-java.zip")

        dependsOn("downloadNewrelic")
        from(zipTree(zipFile))
        into("platform")
        delete(zipFile)
    }

    register("deploy") {
        dependsOn(
            "build",
            "buildDependents",
            "installNewRelic",
        )
    }
}
