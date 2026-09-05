import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.fabric.loom)
	alias(libs.plugins.kotlin)
	alias(libs.plugins.publish)
}

val versionedLibs = the<VersionCatalogsExtension>().find("versionedLibs${sc.current.project.replace(".", "")}").get()
fun VersionCatalog.library(name: String): Provider<MinimalExternalModuleDependency> = this.findLibrary(name).get()
fun VersionCatalog.version(name: String): VersionConstraint = this.findVersion(name).get()

version = providers.gradleProperty("mod_version").get() + "+" + sc.current.version
group = providers.gradleProperty("maven_group").get()
base.archivesName = rootProject.name

repositories {
	maven("https://maven.operationpotato.com/mirror")

	maven("https://maven.teamresourceful.com/repository/maven-public/") {
		content {
			includeGroupByRegex("tech\\.thatgravyboat.*")
			includeGroup("me.owdding")
		}
	}
	maven("https://api.modrinth.com/maven") {
		content {
			includeGroup("maven.modrinth")
		}
	}
	maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") {
		content {
			includeGroup("me.djtheredstoner")
		}
	}
	maven("https://repo.hypixel.net/repository/Hypixel/") {
		content {
			includeGroup("net.hypixel")
		}
	}
	maven("https://maven.terraformersmc.com/releases") {
		content {
			includeGroup("com.terraformersmc")
		}
	}
}

dependencies {
	minecraft(versionedLibs.library("minecraft"))

	implementation(libs.fabric.loader)
	implementation(libs.fabric.language.kotlin)

	implementation(versionedLibs.library("fabric.api"))
	api(versionedLibs.library("skyblock.api")) {
		capabilities {
			requireCapability("tech.thatgravyboat:skyblock-api-${sc.current.project}")
		}
	}
	include(versionedLibs.library("skyblock.api")) {
		capabilities {
			requireCapability("tech.thatgravyboat:skyblock-api-${sc.current.project}")
		}
	}

	includeImplementation(libs.keval)
	includeImplementation(libs.lattice)

	compileOnly(versionedLibs.library("modmenu"))
}

fun DependencyHandlerScope.includeImplementation(dependencyNotation: Provider<*>) {
	this.include(dependencyNotation)
	this.implementation(dependencyNotation)
}

loom {
	runConfigs["client"].apply {
		ideConfigGenerated(true)
		runDir = "../../run"
		val modsDir = rootProject.projectDir.resolve("run/mods/${sc.current.project}/")
		vmArg("-Dfabric.modsFolder=\"$modsDir\"")
	}

	accessWidenerPath = sc.process(
		rootProject.file("src/main/resources/skyblock-item-list.classtweaker"),
		"build/skyblock-item-list.classtweaker"
	)
}

tasks.processResources {
	inputs.property("version", project.property("version"))
	inputs.property("fabric_loader", libs.versions.fabric.loader)
	inputs.property("sbapi", versionedLibs.version("skyblock.api"))
	inputs.property("fabric_api", versionedLibs.version("fabric.api"))
	inputs.property("minecraft", versionedLibs.version("mcRange"))

	filesMatching("fabric.mod.json") {
		val props = mapOf(
			"version" to inputs.properties["version"],
			"fabric_loader" to inputs.properties["fabric_loader"],
			"sbapi" to inputs.properties["sbapi"],
			"fabric_api" to inputs.properties["fabric_api"],
			"minecraft" to inputs.properties["minecraft"],
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
	doLast {
		val files = listOf("${base.archivesName.get()}-$version.jar", "api/${base.archivesName.get()}-api-$version.jar")
		for (fileName in files) {
			val sourceFile = project.projectDir.resolve("build/libs/${fileName}")
			val targetFile = rootProject.projectDir.resolve("build/libs/${fileName}")
			targetFile.parentFile.mkdirs()
			targetFile.writeBytes(sourceFile.readBytes())
		}
	}
}

tasks.named("clean") {
	doLast {
		val libsFolder = rootProject.projectDir.resolve("build/libs")
		if (!libsFolder.exists()) return@doLast
		delete(libsFolder)
	}
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
