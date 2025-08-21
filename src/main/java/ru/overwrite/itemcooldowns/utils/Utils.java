package ru.overwrite.itemcooldowns.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@UtilityClass
public class Utils {

    public List<World> getWorldList(List<String> worldNames) {
        if (!worldNames.isEmpty() && worldNames.get(0).equals("*")) {
            return Bukkit.getWorlds();
        }
        final List<World> worldList = new ArrayList<>(worldNames.size());
        for (String w : worldNames) {
            worldList.add(Bukkit.getWorld(w));
        }
        return worldList;
    }

    public Set<Material> createMaterialSet(List<String> stringList) {
        Set<Material> materialSet = EnumSet.noneOf(Material.class);
        for (String s : stringList) {
            Material mat = Material.matchMaterial(s);
            if (mat != null) {
                materialSet.add(mat);
            }
        }
        return materialSet;
    }

    public Set<PotionEffectType> getPotionEffectSet(List<String> effectNames) {
        if (effectNames.isEmpty() || (effectNames.size() == 1 && effectNames.get(0).equals("*"))) {
            return Set.of();
        }
        Set<PotionEffectType> effects = new HashSet<>();
        for (String name : effectNames) {
            PotionEffectType effect = PotionEffectType.getByName(name.toUpperCase());
            if (effect != null) {
                effects.add(effect);
            }
        }
        return effects;
    }

    public String[] getWorkFactorsAsStringArray(String str) {
        return str.contains(";")
                ? str.trim().split(";")
                : new String[]{str.trim()};
    }
}
