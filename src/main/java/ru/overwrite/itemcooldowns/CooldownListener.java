package ru.overwrite.itemcooldowns;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import ru.overwrite.itemcooldowns.groups.CooldownGroup;
import ru.overwrite.itemcooldowns.groups.WorkFactor;
import ru.overwrite.itemcooldowns.utils.TimedExpiringMap;

public final class CooldownListener implements Listener {

    private final ItemCooldowns plugin;
    private final CooldownManager cooldownManager;

    public CooldownListener(ItemCooldowns plugin) {
        this.plugin = plugin;
        this.cooldownManager = plugin.getCooldownManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        PlayerInventory pInv = p.getInventory();
        WorkFactor factor = WorkFactor.fromAction(event.getAction());
        if (factor == null) {
            return;
        }
        runCooldownTask(event, p, pInv.getItemInMainHand(), factor);
        runCooldownTask(event, p, pInv.getItemInOffHand(), factor);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        PlayerInventory inventory = p.getInventory();
        ItemStack mainHandItem = inventory.getItemInMainHand();
        ItemStack offHandItem = inventory.getItemInOffHand();
        if (p.hasCooldown(mainHandItem.getType()) || p.hasCooldown(offHandItem.getType())) {
            event.setCancelled(true);
        }
        runCooldownTask(event, p, mainHandItem, WorkFactor.ENTITY_INTERACT);
        runCooldownTask(event, p, offHandItem, WorkFactor.ENTITY_INTERACT);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        runCooldownTask(event, p, item, WorkFactor.CONSUME);
    }

    private void runCooldownTask(Cancellable event, Player player, ItemStack item, WorkFactor factor) {
        if (item.getType().isAir()) {
            return;
        }
        if (cooldownManager.isPotion(item.getType())) {
            for (CooldownGroup group : cooldownManager.getCooldownGroups()) {
                TimedExpiringMap<String, ItemStack> cooldowns = group.playerCooldowns();
                if (cooldowns == null) {
                    continue;
                }
                ItemStack cooldownItem = cooldowns.get(player.getName());
                if (cooldownItem != null) {
                    PotionMeta meta = (PotionMeta) cooldownItem.getItemMeta();
                    if (meta.equals(item.getItemMeta())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        Bukkit.getScheduler().runTask(plugin, () -> cooldownManager.process(player, item, factor));
    }
}
