import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.fabric.loom)
	alias(libs.plugins.kotlin)
	alias(libs.plugins.publish)
}

version = providers.gradleProperty("mod_version").get() + "+" + libs.versions.minecraft.get()
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

	includeImplementation(libs.keval)
	includeImplementation(libs.lattice)
}

fun DependencyHandlerScope.includeImplementation(dependencyNotation: Provider<*>) {
	this.include(dependencyNotation)
	this.implementation(dependencyNotation)
}

loom {
	runConfigs["client"].apply {
		ideConfigGenerated(true)
	}

	accessWidenerPath = file("src/main/resources/skyblock-item-list.classtweaker")
}

tasks.processResources {
	inputs.property("version", project.property("version"))
	inputs.property("sbapi", libs.skyblock.api.get().version)

	filesMatching("fabric.mod.json") {
		val props = mapOf(
			"version" to inputs.properties["version"],
			"sbapi" to inputs.properties["sbapi"],
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

tasks.register<Jar>("apiJar") {
	description = "Assembles a jar with only API classes"
	archiveBaseName = "${rootProject.name}-api"
	destinationDirectory = layout.buildDirectory.dir("libs/api")
	from(sourceSets.main.get().output) {
		include("com/operationpotato/itemlist/api/**")
	}
}

tasks.register<Jar>("apiSourcesJar") {
	description = "Assembles a jar with only API sources"
	archiveBaseName = "${rootProject.name}-api"
	archiveClassifier.set("sources")
	destinationDirectory = layout.buildDirectory.dir("libs/api")
	from(sourceSets.main.get().allSource) {
		include("com/operationpotato/itemlist/api/**")
	}
}

tasks.named("assemble") {
	dependsOn("apiJar", "apiSourcesJar")
}

publishing {
	val isFullRelease = System.getenv("IS_FULL_RELEASE") == "true"
	repositories {
		maven {
			val repo = if (isFullRelease) "releases" else "snapshots"
			url = uri("https://maven.operationpotato.com/$repo")
			credentials {
				username = System.getenv("MAVEN_USER")
				password = System.getenv("MAVEN_TOKEN")
			}
		}
	}
	publications {
		register<MavenPublication>("api") {
			pom {
				name.set("ItemList-API")
				url.set("https://github.com/OperationPotato/ItemList")
				if (!isFullRelease) {
					version += "-SNAPSHOT"
				}
				artifactId = "${rootProject.name}-api"
			}
			artifact(tasks.named("apiJar"))
			artifact(tasks.named("apiSourcesJar"))
		}
	}
}
