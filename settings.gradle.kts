pluginManagement {
	repositories {
		maven("https://maven.operationpotato.com/mirror")

		maven("https://maven.operationpotato.com/releases")
		maven("https://maven.fabricmc.net") {
			content {
				includeGroup("net.fabricmc")
			}
		}
		maven("https://maven.kikugie.dev/snapshots") {
			content {
				includeGroup("dev.kikugie")
			}
		}

		mavenCentral()
		gradlePluginPortal()
	}
}

rootProject.name = "skyblock-item-list"

plugins {
	id("dev.kikugie.stonecutter") version "0.10"
	id("com.operationpotato.versioned-catalogs") version "1.0.1"
}

stonecutter {
	create(rootProject) {
		version("26.3")
		version("26.2")
	}
}
