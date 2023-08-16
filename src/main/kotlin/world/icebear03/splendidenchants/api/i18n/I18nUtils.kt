package world.icebear03.splendidenchants.api.i18n

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.chat.ComponentText

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.utils.i18n.I18nUtils
 *
 * @author mical
 * @since 2023/8/16 12:42 PM
 */
fun Player.sendLang(node: String, vararg args: Pair<String, Any>, prefix: Boolean = true) {
    I18n.getLocale(I18n.getLocale(this)).send(this, node, *args, prefix = prefix)
}

fun Player.sendRaw(msg: String, vararg args: Pair<String, Any>, prefix: Boolean = true) {
    I18n.getLocale(I18n.getLocale(this)).send(this, msg, *args, prefix = prefix)
}

fun Player.asLangText(node: String, vararg args: Pair<String, Any>, prefix: Boolean = false) {
    with(I18n.getLocale(I18n.getLocale(this))) {
        if (prefix) {
            cacheWithPrefix(node, *args, player = this@asLangText)
        } else {
            cache(node, *args, player = this@asLangText)
        }
    }
}

fun CommandSender.sendLang(node: String, vararg args: Pair<String, Any>, prefix: Boolean = true) {
    I18n.getLocale(I18n.getLocale()).send(adaptCommandSender(this), node, *args, prefix = prefix)
}

fun CommandSender.sendRaw(msg: String, vararg args: Pair<String, Any>, prefix: Boolean = true) {
    I18n.getLocale(I18n.getLocale()).send(adaptCommandSender(this), msg, *args, prefix = prefix)
}

fun ProxyCommandSender.sendLang(node: String, vararg args: Pair<String, Any>, prefix: Boolean = true) {
    I18n.getLocale(I18n.getLocale(this)).send(this, node, *args, prefix = prefix)
}

fun ProxyCommandSender.sendRaw(msg: String, vararg args: Pair<String, Any>, prefix: Boolean = true) {
    I18n.getLocale(I18n.getLocale(this)).send(this, msg, *args, prefix = prefix)
}

fun ProxyCommandSender.asLangText(node: String, vararg args: Pair<String, Any>, prefix: Boolean = false): ComponentText {
    return with(I18n.getLocale(I18n.getLocale(this))) {
        if (prefix) {
            cacheWithPrefix(node, *args)
        } else {
            cache(node, *args)
        }
    }
}

fun ProxyCommandSender.asLangTextString(node: String, vararg args: Pair<String, Any>): String {
    return I18n.getLocale(I18n.getLocale(this)).origin(node, *args, player = this.castSafely<Player>())
}
