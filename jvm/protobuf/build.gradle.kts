import com.google.protobuf.gradle.protobuf

description = "Ballista protocol buffer format"

plugins {
    java
    id("com.google.protobuf") version "0.9.4"
    id("idea")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.1"
    }
}


sourceSets {
    main {
        proto {
            srcDir("../../proto")
        }
        java{
            srcDir("build/generated/proto/main/java")
        }
    }
}

dependencies {

    implementation(project(":datatypes"))
    implementation(project(":datasource"))
    implementation(project(":logical-plan"))
    implementation(project(":physical-plan"))

    implementation("org.apache.arrow:arrow-memory:0.17.0")
    implementation("org.apache.arrow:arrow-vector:0.17.0")
    implementation("com.google.protobuf:protobuf-java:3.21.1")
    testImplementation("junit:junit:4.13")
}
