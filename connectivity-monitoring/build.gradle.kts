import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.vanniktech.maven.publish)
    id("signing")
}

android {
    namespace = "io.github.sami00777.connectivity_monitoring"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}

// Debug task to check environment variables
tasks.register("checkEnvVars") {
    doLast {
        val signingKey = System.getenv("SIGNING_KEY")
        val signingPassword = System.getenv("SIGNING_PASSWORD")

        println("SIGNING_KEY is ${if (signingKey.isNullOrBlank()) "NOT SET" else "SET (${signingKey.length} characters)"}")
        println("SIGNING_PASSWORD is ${if (signingPassword.isNullOrBlank()) "NOT SET" else "SET"}")
    }
}

mavenPublishing {
    coordinates(
        groupId = "io.github.sami00777",
        artifactId = "connectivity-monitoring",
        version = "1.0.2"
    )

    pom {
        name.set("Connectivity Monitoring")
        description.set("A library for monitoring network connectivity changes in Android applications.")
        url.set("https://github.com/Sami00777/android-connectivity-monitoring")
        inceptionYear.set("2025")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/license/mit/")
            }
        }

        developers {
            developer {
                id.set("sami00777")
                name.set("Sam Shamshiri")
                email.set("sam.sh00777@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/Sami00777/android-connectivity-monitoring")
            connection.set("scm:git:git://github.com/Sami00777/android-connectivity-monitoring.git")
            developerConnection.set("scm:git:ssh://git@github.com/Sami00777/android-connectivity-monitoring.git")
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral(automaticRelease = true)

    // Enable signing for all publications
    signAllPublications()
}

// Configure signing properly
signing {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")

    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

// Configure publications after evaluation
afterEvaluate {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")

    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        signing {
            sign(publishing.publications)
        }
    }
}