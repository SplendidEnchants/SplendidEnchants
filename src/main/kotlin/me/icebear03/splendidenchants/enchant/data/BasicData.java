package me.icebear03.splendidenchants.enchant.data;

import org.bukkit.NamespacedKey;

public class BasicData {
    //唯一标识符，小写下划线
    private String id;
    //游戏中实际显示的名称
    private String name;
    //Bukkit NamespacedKey
    //由id生成，格式为 minecraft:id
    private NamespacedKey key;
    //最高等级
    private int maxLevel;

    public BasicData(String id, String name, int maxLevel) {
        this.id = id;
        this.name = name;
        this.key = NamespacedKey.fromString(id,null);
        this.maxLevel = maxLevel;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public NamespacedKey key() {
        return key;
    }

    public int maxLevel() {
        return maxLevel;
    }
}
