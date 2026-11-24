apply(plugin = "jacoco")

tasks.withType<Test>().configureEach {
    extensions.configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Cobertura de las clases de logica (util, model y LoteMapper) con los tests unitarios"
    dependsOn("testDebugUnitTest")

    val carpetaBuild = layout.buildDirectory.get().asFile

    executionData.setFrom(
        fileTree(carpetaBuild) { include("**/*.exec", "**/*.ec") }
    )
    classDirectories.setFrom(
        files(
            fileTree(File(carpetaBuild, "tmp/kotlin-classes/debug")) {
                include("**/util/**", "**/model/**", "**/data/LoteMapper*")
                exclude("**/*ComposableSingletons*")
            },
            fileTree(File(carpetaBuild, "intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
                include("**/util/**", "**/model/**", "**/data/LoteMapper*")
                exclude("**/*ComposableSingletons*")
            }
        )
    )
    sourceDirectories.setFrom(files("src/main/java"))

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}
