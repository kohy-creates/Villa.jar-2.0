plugins {
    id("java")
}

group = "xyz.kohara"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.dv8tion:JDA:5.3.1") {}
    implementation("org.yaml:snakeyaml:2.4")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.apache.commons:commons-text:1.13.0")
}

tasks.test {
    useJUnitPlatform()
}