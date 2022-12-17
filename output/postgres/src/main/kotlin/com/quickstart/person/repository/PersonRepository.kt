package com.quickstart.person.repository

import com.quickstart.person.dbo.PersonDBO
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.jpa.kotlin.CoroutineJpaSpecificationExecutor
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.POSTGRES)
internal interface PersonRepository: CoroutineCrudRepository<PersonDBO, String>, CoroutineJpaSpecificationExecutor<PersonDBO> {
    fun findByCpf(cpf: String): Mono<PersonDBO>
    fun existsByCpf(cpf: String): Boolean
}