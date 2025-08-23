package ru.overwrite.itemcooldowns.pvpcheckers.impl;

import me.katze.powerantirelog.manager.PvPManager;
import org.bukkit.entity.Player;
import ru.overwrite.itemcooldowns.pvpcheckers.PVPChecker;

public class PowerAntiRelogChecker implements PVPChecker {

    @Override
    public boolean isInPvp(Player player) {
        return PvPManager.isPvP(player);
    }
}
