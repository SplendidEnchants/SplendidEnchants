package me.icebear03.splendidenchants.enchant.data.limitation;

//不同的时候，附魔限制生效范围不一样
//例如使用附魔时无需考虑冲突、依赖，但是获取时需要考虑

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum CheckType {
    //TODO 应当改为可配置 就像2.7那样
    ATTAIN(LimitType.PAPI_EXPRESSION,LimitType.PERMISSION,LimitType.CONFLICT,LimitType.DEPENDENCE), //从战利品/附魔台中获得附魔物品时
    MERCHANT(LimitType.PAPI_EXPRESSION,LimitType.PERMISSION,LimitType.CONFLICT,LimitType.DEPENDENCE), //生成村民交易中的附魔时
    ANVIL(LimitType.CONFLICT,LimitType.DEPENDENCE), //进行铁砧拼合物品附魔时
    USE(LimitType.CONFLICT,LimitType.DEPENDENCE); //使用物品上的附魔时
    CheckType(LimitType... types){
        limitTypes.addAll(Arrays.asList(types));
    }
    public final Set<LimitType> limitTypes=new HashSet<>();
}
