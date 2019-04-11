plugins {
    java
    jacoco
}

version = "1.0-SNAPSHOT"


dependencies {
    compile(project(":core"))
    compile("com.typesafe:config:1.3.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}

jacoco.toolVersion = "0.8.2"

fun jacocoCodeCoverage() {
    val jacocoReport = tasks.withType<JacocoReport> {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(jacocoReport)
    }
}
