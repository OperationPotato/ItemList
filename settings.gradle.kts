pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

rootProject.name = "skyblock-item-list"

dependencyResolutionManagement {
	versionCatalogs {
		create("versionedLibs") {
			from(files("gradle/26_1.versions.toml"))
		}
	}
}

val versions = listOf("26.1")
