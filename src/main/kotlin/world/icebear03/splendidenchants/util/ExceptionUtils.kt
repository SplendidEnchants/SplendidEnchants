package world.icebear03.splendidenchants.util

import world.icebear03.splendidenchants.api.error.ConfigMissingException
import java.io.File

fun missingConfig(file: File, path: String): Nothing {
    throw ConfigMissingException(file, path)
}