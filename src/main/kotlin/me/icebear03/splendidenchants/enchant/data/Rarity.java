package me.icebear03.splendidenchants.enchant.data;

public class Rarity {
    private String id;
    private String name;
    private String color;
    private double weight;

    public Rarity(String id, String name, String color, double weight) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.weight = weight;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public double weight() {
        return weight;
    }

    //TODO attainsources在各个配置文件处理
}
