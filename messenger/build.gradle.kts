version = "1.0-SNAPSHOT"

dependencies {
    compile(project(":server"))
    compile(project(":db"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}
