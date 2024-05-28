plugins {
    id("java")
}

group = "ru.valerit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.7.3")
}

tasks.register<Jar>("fatJar") {
    manifest {
        attributes["Main-Class"] = "server.App"
    }
    archiveBaseName.set("${project.name}-all")
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get())
}

tasks.test {
    useJUnitPlatform()
}