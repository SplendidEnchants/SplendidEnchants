package world.icebear03.splendidenchants

import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency

@RuntimeDependencies(
    RuntimeDependency(
        repository = "http://ptms.ink:8081/repository/releases",
        value = "!com.mcstarrysky.taboolib:module-starrysky:2.0.0-14", // FIXME: 更新 Module-StarrySky 版本后也要在这里更新版本
        relocate = [
            "!com.mcstarrysky.starrysky", "!world.icebear03.splendidenchants.libs.starrysky",
            "!taboolib.", "!world.icebear03.splendidenchants.taboolib.",
            "!kotlin.", "!kotlin1822."
        ],
        test = "!world.icebear03.splendidenchants.libs.starrysky.StarrySky"
    ),
    RuntimeDependency(
        repository = "http://ptms.ink:8081/repository/releases",
        value = "!org.tabooproject.taboolib:module-parrotx:1.5.5",
        relocate = [
            "!org.serverct.parrot.parrotx", "!world.icebear03.splendidenchants.libs.parrotx", // FIXME: 更新 Module-ParrotX 版本后也要在这里更新版本
            "!taboolib.", "!world.icebear03.splendidenchants.taboolib.",
            "!kotlin.", "!kotlin1822."
        ],
        test = "!world.icebear03.splendidenchants.libs.parrotx.ParrotX"
    )
)
object RuntimeEnv