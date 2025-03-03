package ru.overwrite.itemcooldowns.utils.pvp;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.leymooo.antirelog.Antirelog;

public class AntiRelogProvider implements PVPProvider {

    private final Antirelog antirelog;

    public AntiRelogProvider(Plugin plugin) {
        this.antirelog = (Antirelog) plugin;
    }

    @Override
    public boolean isInPvp(Player player) {
        return antirelog.getPvpManager().isInPvP(player);
    }
}
