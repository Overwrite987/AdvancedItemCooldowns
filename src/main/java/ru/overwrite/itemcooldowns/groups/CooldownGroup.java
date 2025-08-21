package ru.overwrite.itemcooldowns.groups;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

public record CooldownGroup(
        String id,
        Set<WorkFactor> workFactors,
        int cooldown,
        List<World> activeWorlds,
        Set<Material> items,
        Set<PotionEffectType> potionEffects,
        boolean ignoreCooldown,
        boolean applyToAll,
        boolean applyOnlyInPvp) {
}
