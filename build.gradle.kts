plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
}

taboolib {
    description {
        contributors {
            name("大白熊_IceBear")
            name("xiaozhangup")
            name("Mical")
        }
        desc("SplendidEnchants 附魔扩展插件")
        load("STARTUP")
    }
    install("common")
    install("common-5")
    install("module-chat")
    install("module-nms")
    install("module-nms-util")
    install("module-kether")
    install("module-configuration")
    install("module-ui")
    install("platform-bukkit")
    classifier = null
    version = "6.0.12-13"

    relocate("org.serverct.parrot.parrotx", "world.icebear03.splendidenchants.taboolib.parrotx")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")

    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11605:11605")
    compileOnly("ink.ptms.core:v11904:11904:mapped")
    compileOnly("ink.ptms.core:v11904:11904:universal")
    compileOnly("ink.ptms.core:v11802:11802:universal")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly("ink.ptms.core:v12001:12001:mapped")
    compileOnly("ink.ptms.core:v12001:12001:universal")

    taboo("org.tabooproject.taboolib:module-parrotx:1.5.4")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}