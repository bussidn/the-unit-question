plugins {
    kotlin("jvm") version "2.3.10"
}

group = "com.theunitquestion"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("io.mockk:mockk:1.13.13")
}

tasks.test {
    useJUnitPlatform()
    // ByteBuddy (used by MockK) needs experimental mode for Java 25
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}

kotlin {
    jvmToolchain(25)
}

