package ru.overwrite.itemcooldowns.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class Utils {

    public List<World> getWorldList(List<String> worldNames) {
        final List<World> worldList = new ArrayList<>(worldNames.size());
        if (worldNames.get(0).equals("*")) {
            worldList.addAll(Bukkit.getWorlds());
            return worldList;
        }
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

    public String[] getWorkFactorsAsStringArray(String str) {
        return str.contains(";")
                ? str.trim().split(";")
                : new String[]{str.trim()};
    }
}
