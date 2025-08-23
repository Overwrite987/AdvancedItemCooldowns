package ru.overwrite.itemcooldowns.groups;

import org.bukkit.event.block.Action;

public enum WorkFactor {

    RIGHT_CLICK_AIR,
    RIGHT_CLICK_BLOCK,
    CONSUME,
    ENTITY_INTERACT;

    public static WorkFactor fromAction(Action action) {
        return switch (action) {
            case RIGHT_CLICK_AIR -> RIGHT_CLICK_AIR;
            case RIGHT_CLICK_BLOCK -> RIGHT_CLICK_BLOCK;
            default -> null;
        };
    }
}
