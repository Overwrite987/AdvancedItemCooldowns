package ru.overwrite.itemcooldowns.groups;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import ru.overwrite.itemcooldowns.utils.TimedExpiringMap;

import java.util.List;
import java.util.Set;

public record CooldownGroup(
        String id,
        Set<WorkFactor> workFactors,
        int cooldown,
        List<World> activeWorlds,
        Set<Material> items,
        Set<PotionEffectType> baseEffects,
        Set<PotionEffectType> potionEffects,
        TimedExpiringMap<String, ItemStack> playerCooldowns,
        boolean ignoreCooldown,
        boolean applyToAll,
        boolean applyOnlyInPvp) {
}
