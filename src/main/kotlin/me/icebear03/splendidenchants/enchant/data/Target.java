package me.icebear03.splendidenchants.enchant.data;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Target {
    public static HashMap<String,Target> targets=new HashMap<>();
    static {
        //TODO INITIALIZE from targets.yml
    }
    private String id;
    private String name;
    private int capability; //该种物品最大的附魔容纳量
    private List<Material> types=new ArrayList<>();

    private Target(String id, String name, int capability, List<String> types) {
        this.id = id;
        this.name = name;
        this.capability = capability;
        for(String string:types) {
            this.types.add(Material.valueOf(string));
        }

        targets.put(id,this);
    }

    public String name() {
        return name;
    }

    public boolean isIn(Material type){
        return types.contains(type);
    }

    public static int maxCapability(Material type){
        int ans=99 ;/*TODO max in config.yml*/
        for(Target target:targets.values()){
            if(target.types.contains(type)){
                ans=Math.min(ans,target.capability);
            }
        }
        return ans;
    }
}
