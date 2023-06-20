package taboolib.module.lang

import me.icebear03.splendidenchants.Config
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer

val adventure by lazy { MiniMessage.miniMessage() }

fun ProxyCommandSender.sendMsg(message: String) {
    val component = adventure.deserialize(message)
    if (Config.useMiniMessage) {
        if (this is ProxyPlayer) {
            castSafely<Player>()?.sendMessage(component)
        } else {
            // 不是玩家就是控制台, 故不做判断了
            Bukkit.getConsoleSender().sendMessage(component)
        }
    } else {
        sendMessage(message)
    }
}

fun ProxyCommandSender.sendLang(node: String, vararg args: Any) {
    val file = getLocaleFile()
    if (file == null) {
        sendMessage("{$node}")
    } else {
        val type = file.nodes[node]
        if (type != null) {
            type.send(this, *args)
        } else {
            sendMessage("{$node}")
        }
    }
}

fun ProxyCommandSender.asLangText(node: String, vararg args: Any): String {
    return asLangTextOrNull(node, *args) ?: "{$node}"
}

fun ProxyCommandSender.asLangTextOrNull(node: String, vararg args: Any): String? {
    val file = getLocaleFile()
    if (file != null) {
        return (file.nodes[node] as? TypeText)?.asText(this, *args)
    }
    return null
}

fun ProxyCommandSender.asLangTextList(node: String, vararg args: Any): List<String> {
    val file = getLocaleFile()
    return if (file == null) {
        listOf("{$node}")
    } else {
        when (val type = file.nodes[node]) {
            is TypeText -> {
                val text = type.asText(this, *args)
                if (text != null) listOf(text) else emptyList()
            }

            is TypeList -> {
                type.asTextList(this, *args)
            }

            else -> {
                listOf("{$node}")
            }
        }
    }
}

fun ProxyCommandSender.getLocale(): String {
    return if (this is ProxyPlayer) Language.getLocale(this) else Language.getLocale()
}

fun ProxyCommandSender.getLocaleFile(): LanguageFile? {
    val locale = getLocale()
    return Language.languageFile.entries.firstOrNull { it.key.equals(locale, true) }?.value
        ?: Language.languageFile[Language.default]
        ?: Language.languageFile.values.firstOrNull()
}

fun registerLanguage(vararg code: String) {
    Language.addLanguage(*code)
}