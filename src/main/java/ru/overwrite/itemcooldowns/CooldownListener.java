package ru.overwrite.itemcooldowns;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.overwrite.itemcooldowns.groups.WorkFactor;
import ru.overwrite.itemcooldowns.services.CooldownService;
import ru.overwrite.itemcooldowns.pvpcheckers.PVPChecker;

public final class CooldownListener implements Listener {

    private final ItemCooldowns plugin;
    private final PVPChecker pvpChecker;
    private final CooldownService cooldownService;

    public CooldownListener(ItemCooldowns plugin) {
        this.plugin = plugin;
        this.pvpChecker = plugin.getPvpChecker();
        this.cooldownService = plugin.getCooldownService();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        PlayerInventory pInv = p.getInventory();
        WorkFactor factor = WorkFactor.fromAction(event.getAction());
        if (factor == null) return;
        runCooldownTask(p, pInv.getItemInMainHand(), factor);
        runCooldownTask(p, pInv.getItemInOffHand(), factor);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        PlayerInventory inventory = p.getInventory();
        ItemStack mainHandItem = inventory.getItemInMainHand();
        ItemStack offHandItem = inventory.getItemInOffHand();
        if (p.hasCooldown(mainHandItem.getType()) || p.hasCooldown(offHandItem.getType())) {
            event.setCancelled(true);
        }
        runCooldownTask(p, mainHandItem, WorkFactor.ENTITY_INTERACT);
        runCooldownTask(p, offHandItem, WorkFactor.ENTITY_INTERACT);
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        runCooldownTask(p, item, WorkFactor.CONSUME);
    }

    private void runCooldownTask(Player player, ItemStack item, WorkFactor factor) {
        Bukkit.getScheduler().runTask(plugin, () -> cooldownService.process(player, item, factor));
    }
}
