package ru.overwrite.itemcooldowns.pvpcheckers.impl;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.leymooo.antirelog.Antirelog;
import ru.overwrite.itemcooldowns.pvpcheckers.PVPChecker;

public class AntiRelogChecker implements PVPChecker {

    private final Antirelog antirelog;

    public AntiRelogChecker(Plugin plugin) {
        this.antirelog = (Antirelog) plugin;
    }

    @Override
    public boolean isInPvp(Player player) {
        return antirelog.getPvpManager().isInPvP(player);
    }
}
