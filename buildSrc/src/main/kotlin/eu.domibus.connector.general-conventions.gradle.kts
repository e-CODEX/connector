plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://scm.ecodex.eu/artifactory/libs-snapshot")
    }
}

group = "eu.domibus.connector"
version = "6.0.0-SNAPSHOT"

publishing {
    publications.create<MavenPublication>("maven") {
        pom {
            organization {
                name = "European Union Agency for the Operational Management of Large-Scale IT Systems in the Area of Freedom, Security and Justice (eu-LISA)"
                url = "https://www.eulisa.europa.eu/"
            }
            licenses {
                license {
                    name = "EUPL, version 1.2"
                    url = "https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12"
                }
            }
            scm {
                connection = "scm:git:git@github.com:eu-LISA/domibusConnector.git"
                developerConnection = "scm:git:ssh://git@github.com:eu-LISA/domibusConnector.git"
                url = "https://github.com/eu-LISA/domibusConnector"
            }
            ciManagement {
                system = "Github Actions"
                url = "https://github.com/eu-LISA/domibusConnector/actions"
            }
        }
    }

    repositories {
//        maven {
//            val releasesRepoUrl = uri("https://scm.ecodex.eu/artifactory/ecodex-releases")
//            val snapshotsRepoUrl = uri("https://scm.ecodex.eu/artifactory/ecodex-snapshots")
//            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
//        }
        maven{
            //TODO remove. Local repository for testing deployments
            url = uri("file:///C:/Users/aipoc/Desktop/ecodex-def/connector/repo")
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    } else {
        useGpgCmd()
    }

    sign(publishing.publications) // Sign all
}