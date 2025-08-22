package ru.overwrite.itemcooldowns.utils;

import com.google.common.collect.ImmutableSet;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;
import ru.overwrite.itemcooldowns.ItemCooldowns;

import java.util.*;

@UtilityClass
public class Utils {

    public Set<UUID> getWorldUIDs(List<String> worldNames) {
        List<UUID> result;
        if (!worldNames.isEmpty() && worldNames.get(0).equals("*")) {
            final List<World> worlds = Bukkit.getWorlds();
            result = new ArrayList<>(worlds.size());
            for (World w : worlds) {
                result.add(w.getUID());
            }
        } else {
            result = new ArrayList<>(worldNames.size());
            for (String w : worldNames) {
                final World world = Bukkit.getWorld(w);
                if (world != null) {
                    result.add(world.getUID());
                } else {
                    ItemCooldowns.getPluginLogger().warning("Не удалось найти мир с именем '" + w + "'!");
                }
            }
        }
        return ImmutableSet.copyOf(result);
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
