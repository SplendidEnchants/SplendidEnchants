package world.icebear03.splendidenchants.enchant.mechanism.chain

enum class ChainType(val display: String) {
    //在Listener层面处理
    DELAY("延时"), // 格式 - 延时::时间(秒)
    GOTO("跳转"), // 格式 - 冷却::时间(秒):是否通告给玩家

    //在Chain层面中处理
    COOLDOWN("冷却"), // 格式 - 对象::对象地址:修改对象的指令:参数
    CONDITION("条件"), // 格式 - 条件::布尔表达式
    ASSIGNMENT("赋值"), // 格式 - 赋值::变量名:值表达式
    EVENT("事件"), // 格式 - 事件::修改事件的指令:参数
    OPERATION("操作"), // 格式 - 操作::已经注册的操作:参数
    OBJECT("对象"); // 格式 - 跳转::对应步骤的序号(从1开始)

    companion object {
        fun getType(identifier: String?): ChainType? = ChainType.entries.find { it.display == identifier || it.name == identifier }
    }
}