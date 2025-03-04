package ru.overwrite.itemcooldowns;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.overwrite.itemcooldowns.groups.CooldownGroup;
import ru.overwrite.itemcooldowns.groups.WorkFactor;
import ru.overwrite.itemcooldowns.utils.pvp.PVPProvider;

public final class CooldownListener implements Listener {

    private final ItemCooldowns plugin;
    private final PVPProvider pvpProvider;

    public CooldownListener(ItemCooldowns plugin) {
        this.plugin = plugin;
        this.pvpProvider = plugin.getPvpProvider();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        PlayerInventory inventory = p.getInventory();
        ItemStack mainHandItem = inventory.getItemInMainHand();
        ItemStack offHandItem = inventory.getItemInOffHand();
        Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_AIR)) {
            Bukkit.getScheduler().runTask(plugin, () -> { // Should be like this due to interaction cancellation on cooldown apply (WHY BUKKIT!?!?! WHYYYY)
                processCooldown(p, mainHandItem, WorkFactor.RIGHT_CLICK_AIR);
                processCooldown(p, offHandItem, WorkFactor.RIGHT_CLICK_AIR);
            });
        }
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            processCooldown(p, mainHandItem, WorkFactor.RIGHT_CLICK_BLOCK);
            processCooldown(p, offHandItem, WorkFactor.RIGHT_CLICK_BLOCK);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        PlayerInventory inventory = p.getInventory();
        ItemStack mainHandItem = inventory.getItemInMainHand();
        ItemStack offHandItem = inventory.getItemInOffHand();
        if (p.hasCooldown(mainHandItem.getType()) || p.hasCooldown(offHandItem.getType())) {
            event.setCancelled(true);
        }
        processCooldown(p, mainHandItem, WorkFactor.ENTITY_INTERACT);
        processCooldown(p, offHandItem, WorkFactor.ENTITY_INTERACT);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        processCooldown(p, item, WorkFactor.CONSUME);
    }

    private void processCooldown(Player p, ItemStack usedItem, WorkFactor workFactor) {
        if (usedItem.getType().isAir()) {
            return;
        }
        if (p.hasPermission("itemcooldown.bypass")) {
            return;
        }
        Material itemMaterial = usedItem.getType();
        for (CooldownGroup group : plugin.getCooldownGroups()) {
            if (!group.workFactors().contains(workFactor)) {
                continue;
            }
            if (!group.activeWorlds().contains(p.getWorld())) {
                continue;
            }
            if (!group.items().contains(itemMaterial)) {
                continue;
            }
            if (group.applyOnlyInPvp() && !pvpProvider.isInPvp(p)) {
                continue;
            }
            if (group.ignoreCooldown() && p.hasCooldown(itemMaterial)) {
                continue;
            }
            if (group.applyToAll()) {
                for (Material material : group.items()) {
                    p.setCooldown(material, group.cooldown());
                }
            } else {
                p.setCooldown(itemMaterial, group.cooldown());
            }
            break;
        }
    }
}
