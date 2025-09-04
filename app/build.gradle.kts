import java.util.Properties

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.google.services)
}

val localProperties = project.rootProject.file("local.properties")

android {
	namespace = "com.example.chater"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.example.chater"
		minSdk = 25
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		if(localProperties.exists()) {
			val properties = Properties().apply {
				load(localProperties.inputStream())
			}
			manifestPlaceholders["appId"] = (properties["APP_ID"] ?: "")
			buildConfigField("String", "AdMobBannerId", "\"${properties["BANNER_AD"]}\"")
			buildConfigField("String", "AdMobRewardedAd", "\"${properties["BG_AD"]}\"")
		}
	}

	buildTypes {
		debug {
			buildConfigField(
				"String",
				"AdMobBannerId",
				"\"ca-app-pub-3940256099942544/9214589741\""
			)
			buildConfigField(
				"String",
				"AdMobRewardedAd",
				"\"ca-app-pub-3940256099942544/5224354917\""
			)
		}

		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
		buildConfig = true
	}
}

dependencies {


	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
	implementation(libs.kotlin.stdlib.jdk8)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.constraintlayout)
	implementation(libs.material)
	implementation(libs.androidx.datastore.preferences)
	implementation(libs.firebase.messaging.ktx)
	implementation(libs.firebase.functions.ktx)

	implementation(libs.play.services.ads)
	implementation(libs.accompanist.systemuicontroller)

	implementation(platform(libs.firebase.bom))
	implementation(libs.firebase.auth)
	implementation(libs.firebase.firestore)

	implementation(libs.androidx.navigation.compose)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.runtime.livedata)

	implementation(libs.ui)
	implementation(libs.ui.graphics)
	implementation(libs.ui.tooling.preview)
	implementation(libs.material3)
	implementation(libs.androidx.compose.material3)
	testImplementation(libs.junit)
	androidTestImplementation(libs.ui.test.junit4)

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}