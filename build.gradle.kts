plugins {
    application
    java
}
// FYI: project group is not used anywhere.
project.group = "org.github.eirnym.palantir"
project.version = project.properties["projVersion"] ?: "1.0.0"

application {
    mainClass.set("com.palantir.javaformat.java.Main")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(
            listOf("compileJava", "processResources"),
        ) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to application.mainClass,
                    "Implementation-Version" to project.libs.palantir.orNull?.version,
                    "Add-Exports" to arrayOf(
                        "jdk.compiler/com.sun.tools.javac.file",
                        "jdk.compiler/com.sun.tools.javac.main",
                        "jdk.compiler/com.sun.tools.javac.parser",
                        "jdk.compiler/com.sun.tools.javac.tree",
                        "jdk.compiler/com.sun.tools.javac.util",
                        "jdk.compiler/com.sun.tools.javac.code",
                        "jdk.compiler/com.sun.tools.javac.api",
                    ).joinToString(" "),
                ),
            )
        } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
            sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}

dependencies {
    implementation(libs.palantir)
}
