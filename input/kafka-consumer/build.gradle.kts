val kafkaJsonSerializerVersion: String by project

dependencies {
    implementation(project(":domain"))

    implementation("io.micronaut.kafka:micronaut-kafka")
    implementation("io.confluent:kafka-json-serializer:$kafkaJsonSerializerVersion")
    implementation("io.confluent:kafka-json-schema-serializer:$kafkaJsonSerializerVersion")
}
