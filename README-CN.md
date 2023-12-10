# SplendidEnchants

![](https://img.shields.io/github/contributors/splendidenchants/splendidenchants)
![](https://img.shields.io/github/languages/code-size/splendidenchants/splendidenchants)

> 适用于 Spigot 1.16+ 的更多附魔插件.

SplendidEnchants 是一个为 Minecraft（Java 版）提供更多附魔的平台，它的前身是 NereusOpus。但是 SplendidEnchants 不仅仅只是提供了更多的附魔，还提供了一个令人惊奇的的框架来创建属于你自己的独一无二的附魔，例如提供了一些可以操作事件、方块、实体或其他东西的魔法般的工具，通过我们自己的脚本语法帮助你向游戏中添加更多的乐趣。

- 在 2.x 版本中，NereusOpus 起初是针对 Paper 的解决方案，不过现在 SplendidEnchant 业已支持 Spigot.
- 我们使用 GPL-3.0 协议进行开源, 这意味着你可以通过编程来创建属于自己的特性.
- 免费 (提供 **有限的** 服务).

随着 3.0 版本的更新, 我们认为 2.0 的代码过于繁琐且不智能, 这已经变得很麻烦了. 我们需要的是一个用高效的框架精心重写、高度自定义、稳定、易于使用的插件。因此迎来 3.0 版本的大规模更新已成定局，我们重新精心设计了 NereusOpus 的每一个组件，更改名称后，就成为了 SplendidEnchants。

## 作者
- HamsterYDS 白熊 .......... 维护者 (has left)
- xbaimiao 小白 .......... 维护者
- Mical 米擦亮 .......... 维护者
- xiaozhangup 小张 .......... 协作者

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

SplendidEnchants 是免费的，但我们不提供 jar 文件，你可以通过以下步骤自行构建插件。

**Windows:**

```
gradlew.bat clean build
```

**macOS/Linux:**

```
./gradlew clean build
```

构建后的插件文件可以在 `./build/libs` 中被找到.

## Contribution
我们欢迎社区开发者. 你可以通过 Pull Requests 贡献你的代码.
