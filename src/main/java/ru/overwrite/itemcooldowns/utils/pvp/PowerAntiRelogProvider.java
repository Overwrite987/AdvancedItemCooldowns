package ru.overwrite.itemcooldowns.utils.pvp;

import me.katze.powerantirelog.manager.PvPManager;
import org.bukkit.entity.Player;

public class PowerAntiRelogProvider implements PVPProvider {

    @Override
    public boolean isInPvp(Player player) {
        return PvPManager.isPvP(player);
    }
}
