import gradle.kotlin.dsl.accessors._5e38c2f7dd07979bbab878ec75ced36d.cyclonedxBom

plugins {
    id("eu.domibus.connector.general-conventions")
    jacoco
    `jvm-test-suite`
    id("org.cyclonedx.bom") // Plugin https://github.com/gradle/gradle/issues/15383
}


configurations {
    all {
        exclude(group = "ch.qos.logback", module = "logback-classic")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(group = "log4j", module = "log4j")
        //exclude(group = "org.apache.logging.log4j", module = "log4j-core")
        //exclude(group = "org.apache.logging.log4j", module = "log4j-api")
        exclude(group = "org.apache.tomcat", module = "jasper-el")
        exclude(group = "org.apache.tomcat", module = "el-api")
        exclude(group = "org.apache.commons", module = "commons-text")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named<Jar>("sourcesJar") {
    // Include all .java files, including package-info.java
    // from(sourceSets.main.get().allJava)
    // Optionally, include other non-class files if needed
    // from(sourceSets.main.get().resources)
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

val testsJar by tasks.register<Jar>("testsJar") {
    dependsOn(tasks.testClasses)
    dependsOn(tasks.processTestResources)
    archiveClassifier.set("tests")
    from(sourceSets["test"].output)
}

tasks.assemble {
    dependsOn(testsJar)
}

tasks.cyclonedxBom {
    outputFormat = "all"
    includeBomSerialNumber = true
    includeLicenseText = true
    destination = base.libsDirectory.asFile
}

publishing {
    publications {
        // Extend the Maven publication named 'maven' defined by the general conventions plugin
        (this["maven"] as MavenPublication).apply {
            from(components["java"])
            artifact(testsJar)
            artifact(base.libsDirectory.file("bom.xml"))
            artifact(base.libsDirectory.file("bom.json"))
        }
    }
}

tasks.withType<Sign> {
    dependsOn(tasks.cyclonedxBom) // Need to sign the BOM file
}

tasks.withType<GenerateModuleMetadata> {
    dependsOn(tasks.cyclonedxBom) // Need to generate metadata for the BOM file
}

tasks.named<Test>("test") {
    testLogging {
        events("passed")
    }
    include("**/*Test.class")
    exclude("**/IT*.class")
    exclude("**/*IT.class")
    exclude("**/*ITCase.class")
    exclude("**/*DBUnit.class")
    reports.html.outputLocation = layout.buildDirectory.dir("reports/tests")

    description = "Runs the unit tests."
}

val integrationTest by tasks.register<Test>("integrationTest") {
    include("**/IT*.class")
    include("**/*IT.class")
    include("**/*ITCase.class")
    exclude("**/*DBUnit.class")
    exclude("**/*Test.class")
    reports.html.outputLocation = layout.buildDirectory.dir("reports/integrationTests")
    description = "Runs the integration tests."
}

val dbUnitTests by tasks.register<Test>("dbunitTests") {
    include("**/*DBUnit.class")
    exclude("**/*Test.class")
    exclude("**/IT*.class")
    exclude("**/*IT.class")
    exclude("**/*ITCase.class")
    reports.html.outputLocation = layout.buildDirectory.dir("reports/dbUnitTests")
    description = "Runs the DBUnit tests."
}

tasks.withType<Test> {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    useJUnitPlatform()
    group = "verification"
}

tasks.named("check") {
    dependsOn(integrationTest)
    dependsOn(dbUnitTests)
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
    shouldRunAfter(tasks.test) // tests are required to run before generating the report
    shouldRunAfter(integrationTest)
    shouldRunAfter(dbUnitTests)
}
