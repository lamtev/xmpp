allprojects {
    group = "com.lamtev.xmpp"

    repositories {
        jcenter()
    }

    apply(plugin = "java")
    apply(plugin = "jacoco")

    tasks.withType(Test::class) {
        useJUnitPlatform()
    }
}

plugins {
    java
    jacoco
}

tasks {
    val jacocoReport by creating(JacocoReport::class) {
        executionData(
                fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec")
        )

        subprojects.forEach {
            sourceSets(it.sourceSets["main"])
        }

        reports {
            sourceDirectories.from(files(sourceSets["main"].allSource.srcDirs))
            classDirectories.from(files(sourceSets["main"].output))

            listOf(xml, html).forEach {
                it.isEnabled = true
                it.destination = File("$buildDir/reports/jacoco/report.${it.name}")
            }
        }
    }
}
