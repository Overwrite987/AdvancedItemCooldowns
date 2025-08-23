package ru.overwrite.itemcooldowns.pvpcheckers.impl;

import dev.enco.greatcombat.GreatCombat;
import dev.enco.greatcombat.manager.CombatManager;
import org.bukkit.entity.Player;
import ru.overwrite.itemcooldowns.pvpcheckers.PVPChecker;

public class GreatCombatChecker implements PVPChecker {

    private final CombatManager combatManager = GreatCombat.getInstance().getCombatManager();

    @Override
    public boolean isInPvp(Player player) {
        return combatManager.isInCombat(player.getUniqueId());
    }
}
