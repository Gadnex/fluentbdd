plugins {
    `java-library`
    `maven-publish`
    id("io.spring.nullability") version "0.0.18"
    id("com.diffplug.spotless") version "8.11.0"
    id("pl.allegro.tech.build.axion-release") version "1.21.4"
}

group = "io.github.gadnex"
version = scmVersion.version
description = "Java library used to generate BDD step documentation for JUnit 6 and Spring projects"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.jspecify:jspecify:1.0.1")
    api("org.junit.jupiter:junit-jupiter-api:6.1.3")
    api("org.springframework:spring-context:7.0.10")

    implementation("org.aspectj:aspectjweaver:1.9.26")
    implementation("org.springframework:spring-test:7.0.10")

    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.3")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
    options.compilerArgs.add("-parameters")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.gadnex"
            artifactId = "fluentbdd"
            from(components["java"])
            pom {
                name.set("fluentbdd")
                description.set("Java library used to generate BDD step documentation for JUnit 6 and Spring projects")
                url.set("https://github.com/Gadnex/fluentbdd")
                inceptionYear.set("2026")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("gadnex")
                        name.set("William Gadney")
                    }
                }
                scm {
                    connection.set("scm:git@github.com:Gadnex/fluentbdd.git")
                    developerConnection.set("scm:git:ssh://github.com/Gadnex/fluentbdd.git")
                    url.set("https://github.com/Gadnex/fluentbdd.git")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("maven-release"))
        }
    }
}