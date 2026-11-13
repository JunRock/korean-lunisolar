import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "2.2.20"
    `java-library`
    signing
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

val repoUrl = "https://github.com/JunRock/korean-lunisolar"

group = "io.github.junrock"
version = "0.0.2"

repositories { mavenCentral() }

/*
 * Java 17 바이트코드로 빌드, 사용처가 17이어도 호환
 */
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xjdk-release=17")
        // Kotlin 2.0 이상을 쓰는 프로젝트에서도 불러 쓸 수 있게 낮춤
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
    }
    coreLibrariesVersion = "2.0.21"
}
tasks.jar { manifest { attributes("Automatic-Module-Name" to "io.github.junrock.lunisolar") } }
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
