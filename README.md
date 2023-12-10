# SplendidEnchants

[**[中文版本/Chinese Ver.]**](https://github.com/SplendidEnchants/SplendidEnchants/blob/master/README-CN.md)

![](https://img.shields.io/github/contributors/splendidenchants/splendidenchants)
![](https://img.shields.io/github/languages/code-size/splendidenchants/splendidenchants)

> A splendid plugin for more enchants in spigot 1.16+.

SplendidEnchants is a enchantment framework for Minecraft Java Version, whose predecessor is NereusOpus 2.0. However, SplendidEnchants itself provides not only lots of new enchantments, but also an amazing framework for creating unique enchantments of you own, like providing some magic tools to access to events, blocks, entities or other objects, helping you add more fun to the game through our own defined special script syntax.

- In 2.x, NereusOpus started out for Paper only, but now SplendidEnchant supported Spigot.
- SplendidEnchants is offered under the GPL-3.0 License, which means you can create your own features by coding.
- Free (with **limited** services).

Along with the 3.0 update, we believe that the 2.0 code is too cumbersome and unintelligent, this has become troublesome. A plugin which is exquisitely rewritten with efficient framework, which is highly customizable, which is stable, which is easy-to-use, is what we need. So it was a foregone conclusion that a huge update to v3.0 was in order, in which we carefully redesigned every single component of NereusOpus, so it just becomes SplendidEnchants after we redesigned and changed the name.

## Authors
- HamsterYDS .......... Maintainer (has left)
- xbaimiao .......... Maintainer
- Mical .......... Maintainer
- xiaozhangup .......... Collaborator

## API

```kotlin
repositories {
    maven("https://repo.tabooproject.org/repository/releases/")
}

dependencies {
    compileOnly("public:SplendidEnchants:3.0.0-2")
}
```

## Building

SplendidEnchants is free, but we do not provide jar files. You can build the plugin yourself by following these steps.

**Windows:**

```
gradlew.bat clean build
```

**macOS/Linux:**

```
./gradlew clean build
```

Build artifacts should be found in `./build/libs` folder.

## Contribution
We are welcome to community developers. You can contribute your code by Pull Requests.
