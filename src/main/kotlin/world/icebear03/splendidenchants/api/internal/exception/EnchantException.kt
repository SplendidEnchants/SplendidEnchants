package world.icebear03.splendidenchants.api.internal.exception

import com.mcstarrysky.starrysky.i18n.I18n
import java.io.PrintStream
import java.io.PrintWriter

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.api.internal.exception.EnchantException
 *
 * @author mical
 * @since 2023/8/16 12:02 PM
 */
open class EnchantException(msg: String) : Throwable(msg) {

    override fun printStackTrace() {
        I18n.printStackTrace(this, null)
    }

    override fun printStackTrace(s: PrintStream?) {
        printStackTrace()
    }

    override fun printStackTrace(s: PrintWriter?) {
        printStackTrace()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun severe(msg: String): Nothing {
    throw EnchantException(msg)
}