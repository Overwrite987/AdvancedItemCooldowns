package ru.overwrite.itemcooldowns;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import ru.overwrite.itemcooldowns.groups.CooldownGroup;
import ru.overwrite.itemcooldowns.groups.WorkFactor;
import ru.overwrite.itemcooldowns.pvpcheckers.PVPChecker;
import ru.overwrite.itemcooldowns.utils.TimedExpiringMap;
import ru.overwrite.itemcooldowns.utils.Utils;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class CooldownManager {

    private final ItemCooldowns plugin;
    private final PVPChecker pvpChecker;
    @Getter
    private Set<CooldownGroup> cooldownGroups;

    public CooldownManager(ItemCooldowns plugin) {
        this.plugin = plugin;
        this.pvpChecker = plugin.getPvpChecker();
    }

    public void setupCooldownGroups() {
        FileConfiguration config = plugin.getConfig();
        long startTime = System.currentTimeMillis();
        ImmutableSet.Builder<CooldownGroup> cooldownGroupsBuilder = ImmutableSet.builder();
        for (String groupId : config.getConfigurationSection("cooldown_groups").getKeys(false)) {
            ConfigurationSection groupSection = config.getConfigurationSection("cooldown_groups." + groupId);
            Set<WorkFactor> workFactors = EnumSet.noneOf(WorkFactor.class);
            for (String workFactor : Utils.getWorkFactorsAsStringArray(groupSection.getString("work_factors"))) {
                workFactors.add(WorkFactor.valueOf(workFactor));
            }
            int cooldown = groupSection.getInt("cooldown", -1);
            if (cooldown < 1) {
                plugin.getLogger().warning("Кулдаун должен быть больше 1. Пропускаем группу " + groupId);
                continue;
            }
            List<World> activeWorlds = Utils.getWorldList(groupSection.getStringList("active_worlds"));
            Set<Material> items = Utils.createMaterialSet(groupSection.getStringList("items"));
            plugin.getLogger().info("Items: " + items);
            if (items.isEmpty()) {
                plugin.getLogger().warning("Нет предметов в группе. Пропускаем группу " + groupId);
                continue;
            }
            Set<PotionEffectType> potionEffects = Utils.getPotionEffectSet(groupSection.getStringList("potion_effects"));
            TimedExpiringMap<String, ItemStack> playerCooldowns = null;
            if (!potionEffects.isEmpty()) {
                playerCooldowns = new TimedExpiringMap<>(TimeUnit.MILLISECONDS);
            }
            boolean ignoreCooldown = groupSection.getBoolean("ignore_cooldown", true);
            boolean applyToAll = groupSection.getBoolean("apply_to_all", false);
            boolean applyOnlyInPvp = plugin.hasPvpProvider() && groupSection.getBoolean("apply_only_in_pvp", false);
            cooldownGroups.add(
                    new CooldownGroup(
                            groupId,
                            workFactors,
                            cooldown,
                            activeWorlds,
                            items,
                            potionEffects,
                            playerCooldowns,
                            ignoreCooldown,
                            applyToAll,
                            applyOnlyInPvp
                    )
            );
        }
        this.cooldownGroups = cooldownGroupsBuilder.build();
        long endTime = System.currentTimeMillis();
        plugin.getLogger().info("Создано " + cooldownGroups.size() + " групп кулдауна за " + (endTime - startTime) + " мс");
    }

    public void process(Player player, ItemStack item, WorkFactor workFactor) {
        for (CooldownGroup group : cooldownGroups) {
            if (shouldApplyCooldown(player, item, workFactor, group)) {
                applyCooldown(player, item, group);
                return;
            }
        }
    }

    private boolean shouldApplyCooldown(Player player, ItemStack item, WorkFactor workFactor, CooldownGroup group) {
        if (!group.workFactors().contains(workFactor)) {
            return false;
        }
        if (!group.activeWorlds().contains(player.getWorld())) {
            return false;
        }
        Material material = item.getType();
        if (!group.items().contains(material)) {
            return false;
        }
        if (group.applyOnlyInPvp() && !pvpChecker.isInPvp(player)) {
            return false;
        }
        if (group.ignoreCooldown() && player.hasCooldown(material)) {
            return false;
        }
        if (isPotion(material) && !potionMatches(item, group.potionEffects())) {
            return false;
        }
        return true;
    }

    private void applyCooldown(Player player, ItemStack item, CooldownGroup group) {
        if (isPotion(item.getType()) && !group.potionEffects().isEmpty()) {
            group.playerCooldowns().put(player.getName(), item, group.cooldown() * 50L);
            return;
        }
        if (group.applyToAll()) {
            for (Material material : group.items()) {
                player.setCooldown(material, group.cooldown());
            }
        } else {
            player.setCooldown(item.getType(), group.cooldown());
        }
    }

    public boolean isPotion(Material material) {
        return material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION;
    }

    private boolean potionMatches(ItemStack item, Set<PotionEffectType> allowedEffects) {
        if (allowedEffects.isEmpty()) {
            return true;
        }
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PotionEffectType baseEffects = meta.getBasePotionData().getType().getEffectType();
        return allowedEffects.contains(baseEffects);
    }
}

