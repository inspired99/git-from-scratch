plugins {
    java
    application
}

group = "org.itmo.java"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("com.beust:jcommander:1.82") // Jcommander
    implementation ("com.googlecode.json-simple:json-simple:1.1.1") // JSON
    implementation ("org.json:json:20171018") // JSON
    implementation("commons-codec:commons-codec:1.11") // утилиты для хеширования
    implementation("commons-io:commons-io:2.6") // утилиты для работы с IO
    implementation("commons-cli:commons-cli:1.4") // фреймворк для создания CLI
    testImplementation(platform("org.junit:junit-bom:5.8.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("ru.itmo.mit.git.Main")
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    test {
        java.setSrcDirs(listOf("test"))
        resources.setSrcDirs(listOf("testResources"))
    }
}

tasks.compileJava {
    options.release.set(11)
}

tasks.test {
    useJUnitPlatform()
}
