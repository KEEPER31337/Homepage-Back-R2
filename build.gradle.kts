import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.asciidoctor.jvm.convert") version "3.3.2"

    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

/******* Start Spring Rest Docs *******/

val asciidoctorExt: Configuration by configurations.creating
val snippetsDir by extra { "$buildDir/generated-snippets" }

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
    test {
        useJUnitPlatform()
        outputs.dir(snippetsDir)
    }
    asciidoctor {
        doFirst {
            project.delete(files("src/main/resources/static/docs"))
        }
        inputs.dir(snippetsDir)
        configurations("asciidoctorExt")
        dependsOn(test)
        baseDirFollowsSourceFile()
    }
    register<Copy>("copyDocs") {
        dependsOn(asciidoctor)
        from("${asciidoctor.get().outputDir}/html5")
        into("src/main/resources/static/docs")
    }
    bootJar {
        dependsOn(asciidoctor)
        from("${asciidoctor.get().outputDir}/html5") {
            into("static/docs")
        }
    }
    build {
        dependsOn("copyDocs")
    }
}
/******* End Spring Rest Docs *******/

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.8.1")

    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
    val kotestVersion = "4.4.3"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:${kotestVersion}")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${kotestVersion}")
    testImplementation("io.kotest:kotest-extensions-spring:${kotestVersion}")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("com.ninja-squad:springmockk:4.0.0")
}
