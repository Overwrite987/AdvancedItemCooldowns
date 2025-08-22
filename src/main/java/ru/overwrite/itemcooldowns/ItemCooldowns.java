package ru.overwrite.itemcooldowns;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.overwrite.itemcooldowns.groups.CooldownGroup;
import ru.overwrite.itemcooldowns.services.CooldownService;
import ru.overwrite.itemcooldowns.pvpcheckers.*;

import java.util.Set;

@Getter
public final class ItemCooldowns extends JavaPlugin {

    private Set<CooldownGroup> cooldownGroups;
    private PVPChecker pvpChecker;
    private CooldownService cooldownService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager pluginManager = getServer().getPluginManager();
        pvpChecker = PVPChecker.get(this);
        cooldownService = new CooldownService(this);
        pluginManager.registerEvents(new CooldownListener(this), this);
        getServer().getScheduler().runTaskAsynchronously(this, () -> cooldownGroups = CooldownGroup.create(this));
        getCommand("advanceditemcooldowns").setExecutor((sender, command, label, args) -> {
            reloadConfig();
            cooldownGroups = CooldownGroup.create(this);
            sender.sendMessage("§aУспешно перезагружено!");
            return true;
        });
        new Metrics(this, 24928);
    }

    public boolean hasPvpProvider() {
        return pvpChecker != null;
    }
}
