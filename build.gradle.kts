@file:Suppress("GradlePackageVersionRange")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("maven-publish")
    id("fabric-loom") version "1.1-SNAPSHOT"
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    idea
}

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "fabric-loom")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "idea")

    val transitiveInclude: Configuration by configurations.creating

    repositories {
        mavenCentral()
        maven("https://repo.repsy.io/mvn/amibeskyfy16/repo") // Use for my JsonConfig lib
    }

    dependencies {
        minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
        mappings("net.fabricmc:yarn:${properties["yarn_mappings"]}:v2")

        modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")
        modImplementation("net.fabricmc:fabric-language-kotlin:${properties["fabric_kotlin_version"]}")

        transitiveInclude(implementation("ch.skyfy.json5configlib:json5-config-lib:1.0.21")!!)

        handleIncludes(project, transitiveInclude)

        testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0")
    }

    tasks {
        val javaVersion = JavaVersion.VERSION_17

        processResources {
            inputs.property("version", rootProject.version)
            filteringCharset = "UTF-8"
            filesMatching("fabric.mod.json") {
                expand(mutableMapOf("version" to rootProject.version))
            }
        }

        java { withSourcesJar() }

//        named<Wrapper>("wrapper") {
//            gradleVersion = "7.6"
//            distributionType = Wrapper.DistributionType.BIN
//        }

        named<KotlinCompile>("compileKotlin") {
            kotlinOptions.jvmTarget = javaVersion.toString()
        }

        named<JavaCompile>("compileJava") {
            options.encoding = "UTF-8"
            options.release.set(javaVersion.toString().toInt())
        }

        named<Jar>("jar") {
            from("LICENSE") {
                rename { "${it}_${base.archivesName}" }
            }
        }

        named<Test>("test") {
            useJUnitPlatform()

            testLogging {
                outputs.upToDateWhen { false } // When the build task is executed, stderr-stdout of test classes will be show
                showStandardStreams = true
            }
        }
    }

}

base {
    archivesName.set(properties["archives_name"].toString())
    group = property("maven_group")!!
    version = property("mod_version")!!
}

repositories {
    mavenCentral()
    maven("https://repo.repsy.io/mvn/amibeskyfy16/repo")
}

dependencies {
//    implementation(project(path = ":api", configuration = "namedElements"))?.let { include(it) }
    implementation(project(path = ":api", configuration = "namedElements"))
}

tasks {

    processResources { dependsOn(project(":api").tasks.processResources.get()) }

//    publish { finalizedBy(project(":api").tasks.publish.get()) }

    val copyJarToTestServer = register("copyJarToTestServer") {
        println("copy to server")
        copyFile("build/libs/homes-${project.properties["mod_version"]}.jar", project.property("testServerModsFolder") as String)
        copyFile("api/build/libs/homes-api-${project.properties["mod_version"]}.jar", project.property("testServerModsFolder") as String)
    }

    build { doLast { copyJarToTestServer.get() } }

}

fun copyFile(src: String, dest: String) = copy { from(src);into(dest) }

fun DependencyHandlerScope.includeTransitive(
    root: ResolvedDependency?,
    dependencies: Set<ResolvedDependency>,
    fabricLanguageKotlinDependency: ResolvedDependency,
    checkedDependencies: MutableSet<ResolvedDependency> = HashSet()
) {
    dependencies.forEach {
        if (checkedDependencies.contains(it) || (it.moduleGroup == "org.jetbrains.kotlin" && it.moduleName.startsWith("kotlin-stdlib")) || (it.moduleGroup == "org.slf4j" && it.moduleName == "slf4j-api"))
            return@forEach

        if (fabricLanguageKotlinDependency.children.any { kotlinDep -> kotlinDep.name == it.name }) {
            println("Skipping -> ${it.name} (already in fabric-language-kotlin)")
        } else {
            include(it.name)
            println("Including -> ${it.name} from ${root?.name}")
        }
        checkedDependencies += it

        includeTransitive(root ?: it, it.children, fabricLanguageKotlinDependency, checkedDependencies)
    }
}

// from : https://github.com/StckOverflw/TwitchControlsMinecraft/blob/4bf406893544c3edf52371fa6e7a6cc7ae80dc05/build.gradle.kts
fun DependencyHandlerScope.handleIncludes(project: Project, configuration: Configuration) {
    includeTransitive(
        null,
        configuration.resolvedConfiguration.firstLevelModuleDependencies,
        project.configurations.getByName("modImplementation").resolvedConfiguration.firstLevelModuleDependencies
            .first { it.moduleGroup == "net.fabricmc" && it.moduleName == "fabric-language-kotlin" }
    )
}