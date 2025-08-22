package ru.overwrite.itemcooldowns.groups;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;
import ru.overwrite.itemcooldowns.ItemCooldowns;
import ru.overwrite.itemcooldowns.utils.Utils;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record CooldownGroup(
        String id,
        Set<WorkFactor> workFactors,
        int cooldown,
        Set<UUID> activeWorlds,
        Set<Material> items,
        Set<PotionEffectType> potionEffects,
        boolean ignoreCooldown,
        boolean applyToAll,
        boolean applyOnlyInPvp) {

    public static Set<CooldownGroup> create(ItemCooldowns plugin) {
        FileConfiguration config = plugin.getConfig();
        long startTime = System.currentTimeMillis();
        final Set<CooldownGroup> cooldownGroups = new HashSet<>();
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
            Set<UUID> activeWorlds = Utils.getWorldUIDs(groupSection.getStringList("active_worlds"));
            Set<Material> items = Utils.createMaterialSet(groupSection.getStringList("items"));
            if (items.isEmpty()) {
                plugin.getLogger().warning("Нет предметов в группе. Пропускаем группу " + groupId);
                continue;
            }
            Set<PotionEffectType> potionEffects = Utils.getPotionEffectSet(groupSection.getStringList("potion_effects"));
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
                            ignoreCooldown,
                            applyToAll,
                            applyOnlyInPvp
                    )
            );
        }
        Set<CooldownGroup> result = ImmutableSet.copyOf(cooldownGroups);
        long endTime = System.currentTimeMillis();
        plugin.getLogger().info("Создано " + cooldownGroups.size() + " групп кулдауна за " + (endTime - startTime) + " мс");
        return result;
    }
}
