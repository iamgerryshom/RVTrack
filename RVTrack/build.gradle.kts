plugins {
    alias(libs.plugins.android.library)
    id("maven-publish") // For publishing
}

android {
    namespace = "com.wid.rvtrack"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

project.afterEvaluate {
    // Ensure that publishing happens after the release AAR is generated
    publishing {
        publications {
            create<MavenPublication>("libraryProject") {
                groupId = "com.github.iamgerryshom"
                artifactId = "RVTrack"
                version = "lv-1.6.0"
                artifact(layout.buildDirectory.file("outputs/aar/${project.name}-release.aar"))
            }
        }
        repositories {
            maven {
                url = uri("https://jitpack.io")
                credentials {
                    username = findProperty("jitpack.user")?.toString() ?: System.getenv("JITPACK_USER")
                    password = findProperty("jitpack.token")?.toString() ?: System.getenv("JITPACK_TOKEN")
                }
            }
        }
    }

    // Ensure the 'publishLibraryProjectPublicationToMavenRepository' depends on the 'bundleReleaseAar' task
    tasks.named("publishLibraryProjectPublicationToMavenRepository") {
        dependsOn(tasks.named("bundleReleaseAar"))
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}