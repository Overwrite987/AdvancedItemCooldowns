package ru.overwrite.itemcooldowns;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.overwrite.itemcooldowns.pvpcheckers.PVPChecker;

@Getter
public final class ItemCooldowns extends JavaPlugin {

    private PVPChecker pvpChecker;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager pluginManager = getServer().getPluginManager();
        pvpChecker = PVPChecker.get(this);
        cooldownManager = new CooldownManager(this);
        pluginManager.registerEvents(new CooldownListener(this), this);
        getServer().getScheduler().runTaskAsynchronously(this, () -> cooldownManager.setupCooldownGroups());
        getCommand("advanceditemcooldowns").setExecutor((sender, command, label, args) -> {
            reloadConfig();
            cooldownManager.setupCooldownGroups();
            sender.sendMessage("§aУспешно перезагружено!");
            return true;
        });
        new Metrics(this, 24928);
    }

    public boolean hasPvpProvider() {
        return pvpChecker != null;
    }
}
