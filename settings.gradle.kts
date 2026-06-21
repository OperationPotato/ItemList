pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		mavenCentral()
		gradlePluginPortal()
	}
}

rootProject.name = "skyblock-item-list"

plugins {
	id("dev.kikugie.stonecutter") version "0.9.6"
}

stonecutter {
	create(rootProject) {
		version("26.2")
		version("26.1", "26.1.2")
	}
}

val versions = listOf("26.2", "26.1")

dependencyResolutionManagement {
	versionCatalogs {
		versions.forEach {
			create("versionedLibs${it.replace(".", "")}") {
				from(files("gradle/${it.replace(".", "_")}.versions.toml"))
			}
		}
	}
}

