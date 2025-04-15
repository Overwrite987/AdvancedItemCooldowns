package ru.overwrite.itemcooldowns;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.overwrite.itemcooldowns.groups.CooldownGroup;
import ru.overwrite.itemcooldowns.groups.WorkFactor;
import ru.overwrite.itemcooldowns.utils.Utils;
import ru.overwrite.itemcooldowns.utils.pvp.*;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public final class ItemCooldowns extends JavaPlugin {

    private Set<CooldownGroup> cooldownGroups;
    private PVPProvider pvpProvider;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager pluginManager = getServer().getPluginManager();
        setupPvpProvider(pluginManager);
        pluginManager.registerEvents(new CooldownListener(this), this);
        getServer().getScheduler().runTaskAsynchronously(this, () -> setupCooldownGroups(getConfig()));
        getCommand("advanceditemcooldowns").setExecutor((sender, command, label, args) -> {
            reloadConfig();
            setupCooldownGroups(getConfig());
            sender.sendMessage("§aУспешно перезагружено!");
            return true;
        });
        new Metrics(this, 24928);
    }

    private void setupPvpProvider(PluginManager pluginManager) {
        if (pluginManager.isPluginEnabled("AntiRelog")) {
            pvpProvider = new AntiRelogProvider(pluginManager.getPlugin("AntiRelog"));
            getLogger().info("AntiRelog выбран в качестве PvP-провайдера");
            return;
        }
        if (pluginManager.isPluginEnabled("CombatLogX")) {
            pvpProvider = new CombatLogXProvider(pluginManager.getPlugin("CombatLogX"));
            getLogger().info("CombatLogX выбран в качестве PvP-провайдера");
            return;
        }
        if (pluginManager.isPluginEnabled("PowerAntiRelog")) {
            pvpProvider = new PowerAntiRelogProvider();
            getLogger().info("PowerAntiRelog выбран в качестве PvP-провайдера");
            return;
        }
        if (pluginManager.isPluginEnabled("GreatCombat")) {
            pvpProvider = new GreatCombatProvider();
            getLogger().info("GreatCombat выбран в качестве PvP-провайдера");
            return;
        }
        getLogger().warning("Не установлен ни один антирелог-плагин. Невозможно установить PvP-провайдер");
    }

    private void setupCooldownGroups(FileConfiguration config) {
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
                getLogger().warning("Кулдаун должен быть больше 1. Пропускаем группу " + groupId);
                continue;
            }
            List<World> activeWorlds = Utils.getWorldList(groupSection.getStringList("active_worlds"));
            Set<Material> items = Utils.createMaterialSet(groupSection.getStringList("items"));
            if (items.isEmpty()) {
                getLogger().warning("Нет предметов в группе. Пропускаем группу " + groupId);
                continue;
            }
            boolean ignoreCooldown = groupSection.getBoolean("ignore_cooldown", true);
            boolean applyToAll = groupSection.getBoolean("apply_to_all", false);
            boolean applyOnlyInPvp = pvpProvider != null && groupSection.getBoolean("apply_only_in_pvp", false);
            cooldownGroups.add(new CooldownGroup(groupId, workFactors, cooldown, activeWorlds, items, ignoreCooldown, applyToAll, applyOnlyInPvp));
        }
        this.cooldownGroups = ImmutableSet.copyOf(cooldownGroups);
        long endTime = System.currentTimeMillis();
        getLogger().info("Создано " + cooldownGroups.size() + " групп кулдауна за " + (endTime - startTime) + " мс");
    }
}
