package ru.overwrite.itemcooldowns.utils.pvp;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CombatLogXProvider implements PVPProvider {

    private final ICombatLogX combatLogX;

    public CombatLogXProvider(Plugin plugin) {
        this.combatLogX = (ICombatLogX) plugin;
    }

    @Override
    public boolean isInPvp(Player player) {
        ICombatManager combatManager = combatLogX.getCombatManager();
        return combatManager.isInCombat(player);
    }
}
