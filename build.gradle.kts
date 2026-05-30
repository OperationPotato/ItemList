import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.fabric.loom)
	alias(libs.plugins.kotlin)
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()

repositories {
	exclusiveContent {
		forRepository {
			maven("https://maven.teamresourceful.com/repository/maven-public/")
		}
		filter {
			includeGroupByRegex("tech\\.thatgravyboat.*")
			includeGroup("me.owdding")
		}
	}
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
	exclusiveContent {
		forRepository {
			maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
		}
		filter {
			includeGroup("me.djtheredstoner")
		}
	}
	exclusiveContent {
		forRepository {
			maven("https://repo.hypixel.net/repository/Hypixel/")
		}
		filter {
			includeGroup("net.hypixel")
		}
	}
}

dependencies {
	minecraft(libs.minecraft)

	implementation(libs.fabric.loader)
	implementation(libs.fabric.language.kotlin)

	implementation(libs.fabric.api)
	api(libs.skyblock.api) {
		capabilities {
			requireCapability("tech.thatgravyboat:skyblock-api-26.1")
		}
	}
	include(libs.skyblock.api) {
		capabilities {
			requireCapability("tech.thatgravyboat:skyblock-api-26.1")
		}
	}
}

loom {
	runConfigs["client"].apply {
		ideConfigGenerated(true)
	}

	accessWidenerPath = file("src/main/resources/skyblock-item-list.classtweaker")
}

tasks.processResources {
	inputs.property("version", project.property("version"))

	filesMatching("fabric.mod.json") {
		val props = mapOf(
			"version" to inputs.properties["version"]
		)
		expand(props)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 25
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_25
	}
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}
