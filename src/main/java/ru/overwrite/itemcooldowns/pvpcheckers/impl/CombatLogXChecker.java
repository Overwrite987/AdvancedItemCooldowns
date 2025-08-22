package ru.overwrite.itemcooldowns.pvpcheckers.impl;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.overwrite.itemcooldowns.pvpcheckers.PVPChecker;

public class CombatLogXChecker implements PVPChecker {

    private final ICombatLogX combatLogX;

    public CombatLogXChecker(Plugin plugin) {
        this.combatLogX = (ICombatLogX) plugin;
    }

    @Override
    public boolean isInPvp(Player player) {
        ICombatManager combatManager = combatLogX.getCombatManager();
        return combatManager.isInCombat(player);
    }
}
