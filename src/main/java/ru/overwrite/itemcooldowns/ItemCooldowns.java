package ru.overwrite.itemcooldowns;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.overwrite.itemcooldowns.groups.CooldownGroup;
import ru.overwrite.itemcooldowns.groups.WorkFactor;
import ru.overwrite.itemcooldowns.utils.Utils;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public final class ItemCooldowns extends JavaPlugin {

    private Set<CooldownGroup> cooldownGroups;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new CooldownListener(this), this);
        getServer().getScheduler().runTaskAsynchronously(this, () -> setupCooldownGroups(getConfig()));
        getCommand("advanceditemcooldowns").setExecutor((sender, command, label, args) -> {
            reloadConfig();
            setupCooldownGroups(getConfig());
            sender.sendMessage("§aУспешно перезагружено!");
            return true;
        });
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
                getLogger().warning("Cooldown should always be > 1. Skipping group " + groupId);
                continue;
            }
            List<World> activeWorlds = Utils.getWorldList(groupSection.getStringList("active_worlds"));
            Set<Material> items = Utils.createMaterialSet(groupSection.getStringList("items"));
            if (items.isEmpty()) {
                getLogger().warning("No items presents in group. Skipping group " + groupId);
                continue;
            }
            boolean ignoreCooldown = groupSection.getBoolean("ignore_cooldown", true);
            boolean applyToAll = groupSection.getBoolean("apply_to_all", false);
            cooldownGroups.add(new CooldownGroup(groupId, workFactors, cooldown, activeWorlds, items, ignoreCooldown, applyToAll));
        }
        this.cooldownGroups = ImmutableSet.copyOf(cooldownGroups);
        long endTime = System.currentTimeMillis();
        getLogger().info("Created " + cooldownGroups.size() + " cooldown groups in " + (endTime - startTime) + " ms");
    }
}
