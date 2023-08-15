package world.icebear03.splendidenchants.api

infix fun Boolean.so(func: () -> Unit) {
    if (this) func.invoke()
}

infix fun Boolean.or(func: () -> Unit) {
    if (!this) func.invoke()
}