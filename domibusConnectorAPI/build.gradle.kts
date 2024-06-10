import java.io.OutputStream
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

plugins {
    id("eu.domibus.connector.java-conventions")
    //id("org.asciidoctor.convert")
}

val wsdlDeps: Configuration by configurations.creating {
    extendsFrom(configurations.compileClasspath.get())
}

dependencies {
    api(libs.javax.xml.bind.jaxb.api)
    api(libs.jaxb.core) // todo replace with jakarta (clashes with cxf in the Link module)
    api(libs.org.glassfish.jaxb.jaxb.runtime)
    api(libs.javax.xml.ws.jaxws.api)
    api(libs.jakarta.jws.jakarta.jws.api)
    testImplementation(libs.org.apache.cxf.cxf.core)

    wsdlDeps(libs.net.sf.saxon.saxon.he) // For xml
    wsdlDeps(libs.cxf.codegen)
    wsdlDeps(libs.cxf.common)
    wsdlDeps(libs.cxf.databinding)

}

description =
    "Contains the domibusConnectorAPI which declares the web interfaces and provides the transition model. It also provides the WSDL files for the webinterfaces."

tasks.register("transformXml") {
    doLast {
        val inputDir = file("src/main/resources/wsdl/eu.domibus.connector.domain.transition")
        val outputDir = file(layout.buildDirectory.dir("site/xsd"))
        val stylesheet = file("src/xs3p/xs3p.xsl")

        outputDir.mkdirs()

        inputDir.listFiles { file -> file.extension == "xsd" }?.forEach { file ->
            val outputFile = File(outputDir, file.name.replace(".xsd", ".html"))
            val factory = TransformerFactory.newInstance()
            val transformer = factory.newTransformer(StreamSource(stylesheet))
            transformer.transform(StreamSource(file), StreamResult(outputFile))
        }
    }
}

val generatedSourcesDir = layout.buildDirectory.dir("generated-sources")

// Task to generate Java sources from WSDL using JavaExec
val generateWsdlSources by tasks.register("generateWsdlSources") {
    val wsdlOptions = listOf(
        "src/main/resources/wsdl/DomibusConnectorGatewayDeliveryWebService.wsdl",
        "src/main/resources/wsdl/DomibusConnectorGatewaySubmissionWebService.wsdl",
        "src/main/resources/wsdl/DomibusConnectorGatewayWebService.wsdl",
        "src/main/resources/wsdl/DomibusConnectorBackendDeliveryWebService.wsdl",
        "src/main/resources/wsdl/DomibusConnectorBackendWebService.wsdl",
        "src/main/resources/wsdl/DomibusConnectorGatewayDeliverySubmitAsyncService.wsdl"
    )

    //TODO extract and parallelize. Add also Incremental support: https://docs.gradle.org/current/userguide/custom_tasks.html#incremental_tasks

    wsdlOptions.forEach { wsdl ->

        javaexec {
            standardOutput = OutputStream.nullOutputStream() //Mute output
            errorOutput = OutputStream.nullOutputStream()
            mainClass = "org.apache.cxf.tools.wsdlto.WSDLToJava"
            classpath(wsdlDeps.asPath)

            val args = listOf(
                "-quiet",
                "-d", generatedSourcesDir.get().asFile.absolutePath,
                "-wsdlLocation", "classpath:wsdl/${File(wsdl).name}",
                layout.projectDirectory.file(wsdl).asFile.absolutePath
            )
            args(args)
        }

    }

}

sourceSets {
    main {
        java {
            // Include the directory containing generated sources
            srcDir(generatedSourcesDir)
        }
    }
}

tasks.named<JavaCompile>("compileJava") {
    // Use the output of generateWsdlSources as an input for compileJava
    dependsOn("generateWsdlSources")
    // Add the directory containing generated sources to the compilation classpath
    source(generatedSourcesDir)
}

// Tasks to assemble WSDL files
tasks.register<Copy>("copyWsdlAndXsd") {
    from("${projectDir}/src/main/resources/wsdl/") {
        include("**/*.wsdl")
        include("**/*.xsd")
    }
    into(layout.buildDirectory.dir("wsdl"))
}

tasks.register<Copy>("copyPolicy") {
    from("${projectDir}/src/main/resources/wsdl/") {
        include("**/*.policy.xml")
    }
    into(layout.buildDirectory.dir("policy"))
}

val zip by tasks.register<Zip>("zipWsdlFiles"){
    archiveClassifier = "wdsl"
    dependsOn("copyWsdlAndXsd", "copyPolicy")
    from(layout.buildDirectory.dir("wsdl"))
    from(layout.buildDirectory.dir("policy"))
    destinationDirectory.set((layout.buildDirectory.dir("distributions").get().asFile))
}

tasks.named("assemble"){
    dependsOn("zipWsdlFiles")
}

(publishing.publications["maven"] as MavenPublication).artifact(zip)

//TODO Site and asciidoc