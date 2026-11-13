import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.20"
    `java-library`
    signing
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

val repoUrl = "https://github.com/JunRock/korean-lunisolar"

group = "io.github.junrock"
version = "0.0.1"

repositories { mavenCentral() }

/*
 * Java 17 바이트코드로 빌드, 사용처가 17이어도 호환
 */
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xjdk-release=17")
    }
}
tasks.withType<JavaCompile> { options.release.set(17) }

ktlint { version.set("1.5.0") }

signing { useGpgCmd() }

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    pom {
        name.set(project.name)
        description.set("Korean lunar calendar (lunisolar) <-> solar date converter based on KASI data")
        url.set(repoUrl)
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("junrock")
                name.set("SeokJun")
                url.set("https://github.com/JunRock")
            }
        }
        scm {
            url.set(repoUrl)
            connection.set("scm:git:$repoUrl.git")
            developerConnection.set("scm:git:ssh://git@github.com/JunRock/korean-lunisolar.git")
        }
    }
}

dependencies { testImplementation("org.jetbrains.kotlin:kotlin-test") }
tasks.withType<Test> { useJUnitPlatform() }
