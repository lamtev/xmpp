version = "1.0-SNAPSHOT"

dependencies {
    compile("com.typesafe:config:1.3.3")
    compile("org.jetbrains:annotations:17.0.0")
    compile("org.postgresql:postgresql:42.2.5")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}
