package ru.overwrite.itemcooldowns.services;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.overwrite.itemcooldowns.ItemCooldowns;
import ru.overwrite.itemcooldowns.groups.CooldownGroup;
import ru.overwrite.itemcooldowns.groups.WorkFactor;
import ru.overwrite.itemcooldowns.pvpcheckers.PVPChecker;

import java.util.List;
import java.util.Set;

public final class CooldownService {

    private final ItemCooldowns plugin;
    private final PVPChecker pvpChecker;

    public CooldownService(ItemCooldowns plugin) {
        this.plugin = plugin;
        this.pvpChecker = plugin.getPvpChecker();
    }

    public void process(Player player, ItemStack item, WorkFactor workFactor) {
        for (CooldownGroup group : this.plugin.getCooldownGroups()) {
            if (shouldApplyCooldown(player, item, workFactor, group)) {
                applyCooldown(player, item, group);
                return;
            }
        }
    }

    private boolean shouldApplyCooldown(Player player, ItemStack item, WorkFactor workFactor, CooldownGroup group) {
        Material material = item.getType();
        if (!group.workFactors().contains(workFactor)) return false;
        if (!group.activeWorlds().contains(player.getWorld().getUID())) return false;
        if (!group.items().contains(material)) return false;
        if (group.applyOnlyInPvp() && !pvpChecker.isInPvp(player)) return false;
        if (group.ignoreCooldown() && player.hasCooldown(material)) return false;
        if (isPotion(material) && !potionMatches(item, group.potionEffects())) return false;
        return true;
    }

    private void applyCooldown(Player player, ItemStack item, CooldownGroup group) {
        if (group.applyToAll()) {
            for (Material material : group.items()) {
                player.setCooldown(material, group.cooldown());
            }
        } else {
            player.setCooldown(item.getType(), group.cooldown());
        }
    }

    private boolean isPotion(Material material) {
        return material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION;
    }

    private boolean potionMatches(ItemStack item, Set<PotionEffectType> allowedEffects) {
        if (allowedEffects.isEmpty()) return true;
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return false;
        List<PotionEffect> effects = meta.getCustomEffects();
        if (effects.size() != allowedEffects.size()) return false;
        for (PotionEffect effect : effects) {
            if (!allowedEffects.contains(effect.getType())) return false;
        }
        return true;
    }
}

