package com.quickstart.integration.container

import com.quickstart.person.dto.PersonEventTypeDTO
import com.quickstart.person.dto.PersonMessageDTO
import com.quickstart.producer.KafkaProducer
import io.kotest.core.spec.style.AnnotationSpec
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import java.util.UUID
import org.apache.kafka.clients.admin.NewTopic
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

class KGenericContainer(imageName: String): GenericContainer<KGenericContainer>(imageName)

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonIntegrationTest(
    private val kafkaProducer: KafkaProducer
): AnnotationSpec() {
    private val kafkaContainerImage = DockerImageName.parse("confluentinc/cp-kafka:latest")
    private val zookeeperContainerImage = DockerImageName.parse("confluentinc/cp-zookeeper:latest")

    @Container
    val zookeeperContainer: KGenericContainer = KGenericContainer("confluentinc/cp-zookeeper:latest")
        .withNetwork(Network.SHARED)
        .withNetworkAliases("zookeeper")
        .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
        .withLabel("reuse.UUID", "cd3dc6ea-aeb6-4334-9eab-5acc2935a7b6")
        .withReuse(true)

    @Container
    val kafkaContainer: KafkaContainer = KafkaContainer(kafkaContainerImage)
        .withNetwork(Network.SHARED)
        .dependsOn(zookeeperContainer)
        .withExternalZookeeper("zookeeper:2181")
        .withLabel("reuse.UUID", "8650c8ae-9246-11eb-a8b3-0242ac130003")
        .withReuse(true)

    @Container
    val schemaRegistryContainer: KGenericContainer = KGenericContainer("confluentinc/cp-schema-registry:latest")
        .withExposedPorts(8081)
        .dependsOn(zookeeperContainer, kafkaContainer)
        .withNetwork(Network.SHARED)
        .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
        .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
        .withEnv(
            "SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS",
            "PLAINTEXT://${kafkaContainer.networkAliases[0]}:9092"
        )
        .withLabel("reuse.UUID", "76a0d53e-9246-11eb-a8b3-0242ac130003")
        .withReuse(true)

    @BeforeEach
    fun beforeAll() {
        zookeeperContainer.start()
        kafkaContainer.start()
        schemaRegistryContainer.start()
        NewTopic("person-event-create", 1, 1)
        println("BOOTSTRAP: ${kafkaContainer.bootstrapServers}")
    }

    @AfterEach
    fun afterAll() {
        zookeeperContainer.stop()
        kafkaContainer.stop()
        schemaRegistryContainer.stop()
    }

    @Test
    suspend fun `blabla`() {

        val personMessageDTO = PersonMessageDTO(
            UUID.randomUUID(),
            name = "Enrico",
            cpf  = "34349566899",
            email = "enrico@cora.com.br",
            type = PersonEventTypeDTO.CREATE
        )
        kafkaProducer.sendMessage(personMessageDTO.id, personMessageDTO)
    }
}