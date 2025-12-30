import ThemerConstants.ALLOW_THIRD_PARTY_SUBSTRATUM_BUILDS
import ThemerConstants.APK_SIGNATURE_PRODUCTION
import ThemerConstants.BASE_64_LICENSE_KEY
import ThemerConstants.ENABLE_APP_BLACKLIST_CHECK
import ThemerConstants.ENFORCE_GOOGLE_PLAY_INSTALL
import ThemerConstants.SHOULD_ENCRYPT_ASSETS
import ThemerConstants.SUPPORTS_THIRD_PARTY_SYSTEMS

import Util.assets
import Util.cleanEncryptedAssets
import Util.copyEncryptedTo
import Util.generateRandomByteArray
import Util.tempAssets
import Util.twistAsset

import java.util.Random
import java.io.FileInputStream
import java.io.FileOutputStream

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
}

// Themers: DO NOT MODIFY
val secretKey = generateRandomByteArray()
val ivKey = generateRandomByteArray()


// Create a variable called keystorePropertiesFile, and initialize it to your
// keystore.properties file, in the rootProject folder.
val keystorePropertiesFile = rootProject.file("keystore.properties")

// Initialize a new Properties() object called keystoreProperties.
val keystoreProperties = Properties()

// Load your keystore.properties file into the keystoreProperties object.


android {

    compileSdk = 36
    defaultConfig {
        applicationId="tugaia56.dark.shadow.theme"
        minSdk=28
        versionCode = 615
        versionName = "6.1.5"
        setProperty("archivesBaseName", "Dark_Shadow_Theme_v6.1.5")

        // Themers: DO NOT MODIFY
        buildConfigField("boolean", "SUPPORTS_THIRD_PARTY_SYSTEMS", "$SUPPORTS_THIRD_PARTY_SYSTEMS")
        buildConfigField("boolean", "ENABLE_APP_BLACKLIST_CHECK", "$ENABLE_APP_BLACKLIST_CHECK")
        buildConfigField("boolean", "ALLOW_THIRD_PARTY_SUBSTRATUM_BUILDS", "$ALLOW_THIRD_PARTY_SUBSTRATUM_BUILDS")
        buildConfigField("String", "IV_KEY", "\"$ivKey\"")
        buildConfigField("byte[]", "DECRYPTION_KEY", secretKey.joinToString(prefix = "{", postfix = "}"))
        buildConfigField("byte[]", "IV_KEY", ivKey.joinToString(prefix = "{", postfix = "}"))
        resValue("string", "encryption_status", if (shouldEncrypt()) "onCompileVerify" else "false")
    }
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    var releaseSigning = signingConfigs.getByName("debug")

    try {
        val keystoreProperties = Properties()
        FileInputStream(keystorePropertiesFile).use { inputStream ->
            keystoreProperties.load(inputStream)
        }

        releaseSigning = signingConfigs.create("release") {
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            enableV1Signing = true
            enableV2Signing = true
        }
    } catch (_: Exception) {}
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = releaseSigning
            // Themers: DO NOT MODIFY
            buildConfigField("boolean", "ENFORCE_GOOGLE_PLAY_INSTALL", "$ENFORCE_GOOGLE_PLAY_INSTALL")
            buildConfigField("String", "BASE_64_LICENSE_KEY", "\"$BASE_64_LICENSE_KEY\"")
            buildConfigField("String", "APK_SIGNATURE_PRODUCTION", "\"$APK_SIGNATURE_PRODUCTION\"")
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = releaseSigning
            // Themers: DO NOT MODIFY
            buildConfigField("boolean", "ENFORCE_GOOGLE_PLAY_INSTALL", "false")
            buildConfigField("String", "BASE_64_LICENSE_KEY", "\"\"")
            buildConfigField("String", "APK_SIGNATURE_PRODUCTION", "\"\"")
        }
        getByName("debug") {
            versionNameSuffix = ".debug"
        }
    }
    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "Dark_Shadow_Theme.apk"
                output.outputFileName = outputFileName
            }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    sourceSets {
        named("main") {
            java.srcDir("src/main/kotlin")
        }
    }
    namespace = "tugaia56.dark.shadow.user"
    packaging {
        jniLibs.useLegacyPackaging = true
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8", version = Constants.kotlinVersion))

    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.github.javiersantos:PiracyChecker:1.2.5")
}

// Themers: DO NOT MODIFY ANYTHING BELOW
tasks.register("encryptAssets") {
    if (!shouldEncrypt()) {
        println("Skipping assets encryption...")
        return@register
    }

    // Check if temp assets exist
    if (!projectDir.tempAssets.exists()) {
        println("Encrypting duplicated assets, don't worry, your original assets are safe...")

        val secretKeySpec = SecretKeySpec(secretKey, "AES")
        val ivParameterSpec = IvParameterSpec(ivKey)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            .apply {
                init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
            }

        // Encrypt every single file in the assets dir recursively
        projectDir.assets.walkTopDown().filter { it.isFile }.forEach { file ->
            file.twistAsset("assets", "assets-temp")

            //Encrypt assets
            if (file.absolutePath.contains("overlays")) {
                FileInputStream(file).use { fis ->
                    FileOutputStream("${file.absolutePath}.enc").use { fos ->
                        fis.copyEncryptedTo(fos, cipher, bufferSize = 64)
                    }
                }
                file.delete()
            }
        }
    } else {
        throw RuntimeException("Old temporary assets found! Try and do a clean project.")
    }
}

project.afterEvaluate {
    tasks.named("preBuild") {
        dependsOn("encryptAssets")
    }
}

gradle.buildFinished {
    projectDir.cleanEncryptedAssets()
}

fun shouldEncrypt(): Boolean {
    val tasks = project.gradle.startParameter.taskNames
    return SHOULD_ENCRYPT_ASSETS && tasks.joinToString().contains("release", ignoreCase = true)
}