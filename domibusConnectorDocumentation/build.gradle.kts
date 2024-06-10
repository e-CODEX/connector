plugins {
    id("eu.domibus.connector.general-conventions")
    alias(libs.plugins.asciidoctor)
    alias(libs.plugins.asciidoctor.pdf)
}

description = "This module contains the documentation like the create, migrate sql scripts"

val htmlDocuOutputDirectory = "build/classes/META-INF/resources/documentation"

sourceSets {
    named("main") {
        resources {
            srcDir("build/classes")
        }
    }
}

tasks {

    register<Copy>("copyAsciidocResources") {
        from("src/asciidoc/resources") {
            include("**/*.jpg", "**/*.png", "**/*.svg")
        }
        into("$htmlDocuOutputDirectory/../")
        description = "Copy the resources for the documentation"
    }

    processResources {
        dependsOn(asciidoctor)
        dependsOn(compileJava)
    }

    asciidoctorj {
        setVersion(libs.versions.asciidoctorj)
        modules {
            diagram.use()
        }
        attributes(
            mapOf(
                "project-version" to "${project.version}",
                "source-highlighter" to "rouge",
                "rouge-style" to "github",
                "mvnbasedir" to "${projectDir}/../",
                "basepath" to "${projectDir}/src/asciidoc/",
                "webimagedir" to "./images",
                "icons" to "font"
            )
        )
    }

    asciidoctor {  // HTML documentation
        dependsOn("copyAsciidocResources")
        sourceDir(file("src/asciidoc/doc"))
        setOutputDir(file(htmlDocuOutputDirectory))
        description = "Generates the HTML documentation"
    }

    asciidoctorPdf {
        sourceDir(file("src/asciidoc/pdf-doc"))
        setOutputDir(file("build/generated-pdfs"))
        attributes(
            mapOf(
                "imagesdir" to "${projectDir}/src/asciidoc/resources/images"
            )
        )
        baseDirFollowsSourceFile()
        description = "Generates the PDF documentation"
    }

    register<Sync>("prepareDocumentation") {
        dependsOn("copyAsciidocResources")
        dependsOn("asciidoctor")
        dependsOn("asciidoctorPdf")

        from(file(htmlDocuOutputDirectory)) {
            into("online-documentation/documentation")
        }
        from(file("${htmlDocuOutputDirectory}/../images")) {
            into("online-documentation/images")
        }
        from(file("src/main/config")) {
            into("config")
        }
        from(layout.buildDirectory.dir("generated-pdfs")) {
            include("*.pdf")
            into("pdf-documentation")
        }
        into(layout.buildDirectory.dir("assembledDocs"))
        description = "Prepare the documentation for packaging"
    }

    register<Zip>("createDocumentationZip") {
        dependsOn("prepareDocumentation")
        from(layout.buildDirectory.dir("assembledDocs"))
        destinationDirectory = base.libsDirectory
        archiveClassifier = "documentation"
        description = "Creates a zip file with the documentation"
    }

    assemble {
        dependsOn("createDocumentationZip")
    }

    withType<Jar> {
        dependsOn("asciidoctor")
    }
}

publishing {
    publications {
        (this["maven"] as MavenPublication).apply {
            from(components["java"])
            artifact(tasks["createDocumentationZip"])
        }
    }
}