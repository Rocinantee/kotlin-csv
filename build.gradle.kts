plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.41")
    id("org.jetbrains.dokka").version("0.9.18")
    `maven-publish`
    signing
}

group = "com.github.doyaaaaaken"
version = "0.1.0"

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.18")
    }
}

repositories {
    jcenter()
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
}


//publishing settings
//https://docs.gradle.org/current/userguide/publishing_maven.html
val sourcesJar = task<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}
val dokkaJar = task<Jar>("dokkaJar") {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "kotlin-csv"
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
        }
    }
    repositories {
        maven {
            credentials {
                val nexusUsername: String? by project
                val nexusPassword: String? by project
                username = nexusUsername
                password = nexusPassword
            }

            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
