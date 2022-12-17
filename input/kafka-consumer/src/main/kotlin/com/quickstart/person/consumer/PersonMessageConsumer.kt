package com.quickstart.person.consumer

import com.quickstart.person.dto.PersonMessageConsumerDTO
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.messaging.annotation.MessageBody
import java.util.Base64
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DatumReader
import org.apache.avro.io.Decoder
import org.apache.avro.io.DecoderFactory
import org.apache.avro.specific.SpecificDatumReader
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

@KafkaListener(
    offsetReset = OffsetReset.EARLIEST,
    groupId = "person-event-consumer",
    clientId = "person-event-consumer"
)
class PersonMessageConsumer {
    @Topic(PERSON_PRODUCER_TOPIC)
    fun receive(@MessageBody personMessageConsumerDTO: PersonMessageConsumerDTO) {
        logger.info("Person Event Consumed: $personMessageConsumerDTO")
    }

    companion object {
        private const val PERSON_PRODUCER_TOPIC = "person-event-create"
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}

class AvroDeserializer<T : GenericRecord>(private val targetType: Class<T>) : Deserializer<T?> {

    override fun deserialize(topic: String, data: ByteArray): T {

        return try {
            logger.debug("data='${Base64.getEncoder().encode(data)}' ")
            deserializeSpecificRecordRaw(data).also { logger.debug("deserialized data='{$this}')") }
        } catch (error: Exception) {
            logger.debug("data='${Base64.getEncoder().encode(data)}' ")
            deserializeSpecificRecordConfluent(data).also { logger.debug("deserialized data='{$this}' )") }
        }
    }

    private fun deserializeSpecificRecordConfluent(data: ByteArray): T {
        val datumReader: DatumReader<GenericRecord> = SpecificDatumReader(targetType.getDeclaredConstructor().newInstance().schema)
        val removeBytes = 5
        val decoder: Decoder = DecoderFactory.get().binaryDecoder(data, removeBytes, data.size - removeBytes, null)
        return datumReader.read(null, decoder) as T
    }

    private fun deserializeSpecificRecordRaw(data: ByteArray): T {
        val datumReader: DatumReader<GenericRecord> = SpecificDatumReader(targetType.getDeclaredConstructor().newInstance().schema)
        val decoder: Decoder = DecoderFactory.get().binaryDecoder(data, null)
        return datumReader.read(null, decoder) as T
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}