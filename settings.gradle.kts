rootProject.name="micronaut-quickstart"

include("main", "domain")

// Input
include("rest", "kafka-consumer")
project(":rest").projectDir = file("input/rest")
project(":kafka-consumer").projectDir = file("input/kafka-consumer")

// Secondary
include("postgres", "kafka-producer")
project(":postgres").projectDir = file("output/postgres")
project(":kafka-producer").projectDir = file("output/kafka-producer")