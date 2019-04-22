dependencies {
    compile("org.jetbrains:annotations:17.0.0")
    compile("net.sf.trove4j:trove4j:3.0.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}
