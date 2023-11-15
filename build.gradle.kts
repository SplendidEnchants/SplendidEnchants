buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/central")
    }
}

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
            name("Micalhl")
        }
        desc("SplendidEnchants 附魔插件")
        load("STARTUP")
        dependencies {
            name("TrChat").optional(true)
            name("InteractiveChat").optional(true)
        }
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
    version = "6.0.12-26"

    relocate("org.serverct.parrot.parrotx", "world.icebear03.splendidenchants.taboolib.parrotx")
    relocate("com.mcstarrysky.starrysky", "world.icebear03.splendidenchants.taboolib.starrysky")

    options("keep-kotlin-module")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")

    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11605:11605")
    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("ink.ptms.core:v11904:11904:mapped")
    compileOnly("ink.ptms.core:v11904:11904:universal")
    compileOnly("ink.ptms.core:v11802:11802:universal")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v12001:12001:mapped")
    compileOnly("ink.ptms.core:v12001:12001:universal")

    taboo("org.tabooproject.taboolib:module-parrotx:1.5.4") // Module-ParrotX
    taboo("com.mcstarrysky.taboolib:module-starrysky:1.0.12-6") // Module-StarrySky

    // Purtmars Repository
    compileOnly("public:TrChat:2.0.4")
    compileOnly("public:InteractiveChat:4.2.7.2")

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
