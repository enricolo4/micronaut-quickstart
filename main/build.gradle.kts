val kafkaJsonSerializerVersion: String by project
val kotestRunnerVersion: String by project
val micronautVersion: String by project
val mockkVersion: String by project

plugins {
    application
    id("io.micronaut.application") version "2.0.3"
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

kapt {
    arguments {
        arg("micronaut.processing.incremental", true)
        arg("micronaut.processing.annotations", "com.quickstart.*,io.quickstart.*")
    }
}

application {
    mainClass.set("com.quickstart.ApplicationKt")

    applicationDefaultJvmArgs = listOf(
        "-server",
        "-XX:+UseNUMA",
        "-XX:+UseParallelGC",
        "-Duser.timezone=America/Sao_Paulo"
    )
}

micronaut {
    version("3.7.4")
    runtime("netty")
    testRuntime("kotest")

    processing {
        annotations("com.quickstart.*")
    }
}

tasks {
    shadowJar {
        archiveBaseName.set("micronaut-hexagonal-quickstart")
        archiveVersion.set("")
    }
}

val inputProjects = listOf(":rest", ":kafka-consumer")
val outputProjects = listOf(":postgres", ":kafka-producer")
val projects = listOf(":domain") + inputProjects + outputProjects

dependencies {
    projects.map { projects -> project(projects) }
        .forEach { projectDependency -> implementation(projectDependency) }

    kapt("io.micronaut:micronaut-bom:$micronautVersion")
    implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))

    implementation("io.micronaut:micronaut-runtime")

    kaptTest("io.micronaut:micronaut-bom:$micronautVersion")

    testImplementation("io.micronaut.kafka:micronaut-kafka")
    testImplementation("io.confluent:kafka-json-serializer:$kafkaJsonSerializerVersion")
    testImplementation("io.confluent:kafka-json-schema-serializer:$kafkaJsonSerializerVersion")

    testImplementation("io.micronaut.test:micronaut-test-kotest")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestRunnerVersion")
    testImplementation("javax.annotation:javax.annotation-api")
}
