package ru.overwrite.itemcooldowns.pvpcheckers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import ru.overwrite.itemcooldowns.pvpcheckers.impl.AntiRelogChecker;
import ru.overwrite.itemcooldowns.pvpcheckers.impl.CombatLogXChecker;
import ru.overwrite.itemcooldowns.pvpcheckers.impl.GreatCombatChecker;
import ru.overwrite.itemcooldowns.pvpcheckers.impl.PowerAntiRelogChecker;

public interface PVPChecker {

    boolean isInPvp(Player player);

    static PVPChecker get(Plugin plugin) {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        if (pluginManager.isPluginEnabled("AntiRelog")) {
            plugin.getLogger().info("AntiRelog выбран в качестве PvP-провайдера");
            return new AntiRelogChecker(pluginManager.getPlugin("AntiRelog"));
        }
        if (pluginManager.isPluginEnabled("CombatLogX")) {
            plugin.getLogger().info("CombatLogX выбран в качестве PvP-провайдера");
            return new CombatLogXChecker(pluginManager.getPlugin("CombatLogX"));
        }
        if (pluginManager.isPluginEnabled("PowerAntiRelog")) {
            plugin.getLogger().info("PowerAntiRelog выбран в качестве PvP-провайдера");
            return new PowerAntiRelogChecker();
        }
        if (pluginManager.isPluginEnabled("GreatCombat")) {
            plugin.getLogger().info("GreatCombat выбран в качестве PvP-провайдера");
            return new GreatCombatChecker();
        }
        plugin.getLogger().warning("Не установлен ни один антирелог-плагин. Невозможно установить PvP-провайдер");
        return null;
    }

}
