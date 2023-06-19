package me.icebear03.splendidenchants.enchant.data.limitation;

import kotlin.Pair;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Limitations {
    public List<Pair<LimitType,String>> limitations=new ArrayList<>();
    public Limitations(String... lines){
        for(String line:lines){
            String[] split=line.split(":");
            LimitType type=LimitType.valueOf(split[0]);
            String limit=split[1];
            limitations.add(new Pair<>(type,limit));
        }
    }

    //检查操作是否被允许（比如是否可以附魔到某个物品上、使用时是否可以生效、村民生成新交易等）
    //item就是跟操作直接有关的物品（如正在被附魔的书、正在使用的剑、生成的新交易中卖出的附魔书等）
    public Pair<Boolean,String> checkAvaliable(CheckType checkType, LivingEntity creature, ItemStack item){
        for(Pair<LimitType,String> limitation:limitations){
            LimitType limitType=limitation.component1();
            String value=limitation.component2();
            switch (limitType){
                case PAPI_EXPRESSION:
                    //TODO papi表达式的处理
                    //TODO 返回信息记得replace中文，比如 %player_level%>=30 应当翻译为 "经验等级>=30"
                    return new Pair<>(false,"{limit.papi_expression}");
                case PERMISSION:
                    if(!creature.hasPermission(value))
                        return new Pair<>(false,"{limit.permission} || permission="+value);
                case CONFLICT:
                    return new Pair<>(false,"{limit.conflict} || enchant="+value);
                case DEPENDENCE:
                    return new Pair<>(false,"{limit.dependence} || enchant="+value);
            }
        }
        return new Pair<>(true,"");
    }

    //TODO 添加limitation
    //TODO 根据limitation生成附魔介绍（GUI（附属中）、文字输出等）
}
