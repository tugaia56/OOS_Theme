import Util.cleanEncryptedAssets

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:9.0.1")
        classpath(kotlin("gradle-plugin", version = Constants.kotlinVersion))
    }
}

allprojects {
    repositories {
        google()
        maven(url = "https://jitpack.io")
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
    projectDir.cleanEncryptedAssets()
}
