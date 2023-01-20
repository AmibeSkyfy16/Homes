import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

base {
    archivesName.set(properties["archives_name"].toString())
    group = property("maven_group")!!
    version = rootProject.version
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.base.archivesName.get()

            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("https://repo.repsy.io/mvn/amibeskyfy16/repo")
            credentials {
                val properties = Properties()
                properties.load(file("E:\\repsy.properties").inputStream())
                username = "${properties["USERNAME"]}"
                password = "${properties["PASSWORD"]}"
            }
        }
    }
}
