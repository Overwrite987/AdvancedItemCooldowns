package ru.overwrite.itemcooldowns.utils.pvp;

import dev.enco.greatcombat.GreatCombat;
import dev.enco.greatcombat.manager.CombatManager;
import org.bukkit.entity.Player;

public class GreatCombatProvider implements PVPProvider {

    private final CombatManager combatManager = GreatCombat.getInstance().getCombatManager();

    @Override
    public boolean isInPvp(Player player) {
        return combatManager.isInCombat(player.getUniqueId());
    }
}
