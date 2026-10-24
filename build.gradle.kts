import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "4.0.5"

    val kotlinVersion = "2.2.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

tasks.register("downloadDependencies") {
    group = "build setup"
    description = "Downloads dependencies for the Docker build before application sources are copied."

    doLast {
        // Resolve actual JARs, including Kotlin build tools, without compiling sources.
        listOf(
            "compileClasspath",
            "runtimeClasspath",
            "annotationProcessor",
            "kotlinCompilerClasspath",
            "kotlinCompilerPluginClasspathMain",
            "kotlinBuildToolsApiClasspath",
        ).forEach { configurationName ->
            configurations.getByName(configurationName).resolve()
        }
    }
}

/******* Start Spring Rest Docs *******/

val asciidoctorExt: Configuration by configurations.creating
val snippetsDir by extra { "$buildDir/generated-snippets" }

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_21)
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
        from("${asciidoctor.get().outputDir}")
        into("src/main/resources/static/docs")
    }
    bootJar {
        dependsOn(asciidoctor)
        from("${asciidoctor.get().outputDir}") {
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
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:2.0.1")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.apache.tika:tika-core:3.3.3")
    implementation("org.apache.tika:tika-parsers-standard-package:3.3.3")
    implementation("org.apache.commons:commons-lang3:3.20.0")

    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
    val kotestVersion = "6.2.6"
    testImplementation("io.kotest:kotest-assertions-core-jvm:${kotestVersion}")
    testImplementation("io.mockk:mockk:1.14.9")
    testImplementation("com.ninja-squad:springmockk:5.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
