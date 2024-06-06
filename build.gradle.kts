plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ru.valerit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

project(":server") {
    apply (plugin="application")
    configure<JavaApplication> {
        mainClass = "server.App"
    }
    dependencies {
        implementation(project(":common"))
    }
    tasks.withType<Jar> {
        manifest {
            attributes["Main-Class"] = "server.App"
        }
    }
}


project(":client") {
    apply (plugin="application")
    configure<JavaApplication> {
        mainClass = "client.App"
    }
    dependencies {
        implementation(project(":common"))
    }
    val run by tasks.getting(JavaExec::class) {
        standardInput = System.`in`
    }
    tasks.withType<Jar> {
        manifest {
            attributes["Main-Class"] = "client.App"
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.7.3")
}

tasks.create("createpreview") {
    dependsOn("client:fatJar")
    dependsOn("server:fatJar")
}

tasks.create("deploy") {
    dependsOn("client:fatJar")
    dependsOn("server:fatJar")
    doLast {
        val user = (System.getenv("DEPLOYUSER") ?: "ERROR")
        val userAndHost : String = user + "@" + (System.getenv ("DEPLOYHOST")  ?: "ERROR")

        val pwd : String = System.getenv("DEPLOYPWD") ?: "ERROR"

        println("$userAndHost :$pwd")

        exec {
            workingDir(".")
            commandLine("pscp", "-pw", pwd, "-P", 2222, "C:/Users/User/Desktop/lab7out/**.jar", "$userAndHost:/home/studs/$user/lab3db")
//            commandLine("putty.exe", "-ssh", userAndHost, "-P", "2222", "-pw", pwd)
        }
    }
}

tasks.test {
    useJUnitPlatform()
}