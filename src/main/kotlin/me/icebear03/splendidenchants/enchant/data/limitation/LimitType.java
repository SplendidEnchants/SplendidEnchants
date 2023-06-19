package me.icebear03.splendidenchants.enchant.data.limitation;

public enum LimitType {
    //TODO 应当自定义语言
    PAPI_EXPRESSION("表达式"), //如 %player_level%>=30
    PERMISSION("权限"), //如 splendidenchants.use
    CONFLICT("冲突"), //如 锋利
    DEPENDENCE("依赖"); //如 无限
    LimitType(String name){
        this.name=name;
    }
    public final String name;
}
