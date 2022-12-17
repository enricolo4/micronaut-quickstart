dependencies {
    implementation(project(":domain"))

    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("jakarta.persistence:jakarta.persistence-api:3.1.0")

    implementation("io.micronaut.r2dbc:micronaut-r2dbc-core")
    implementation("io.micronaut.data:micronaut-data-r2dbc")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.flyway:micronaut-flyway")

    implementation("io.r2dbc:r2dbc-postgresql:0.8.13.RELEASE")
    runtimeOnly("org.postgresql:postgresql")

    kaptTest("io.micronaut.data:micronaut-data-processor")
    kaptTest("jakarta.persistence:jakarta.persistence-api:3.1.0")
}
