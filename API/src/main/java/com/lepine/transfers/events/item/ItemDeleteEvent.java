package com.lepine.transfers.events.item;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;


public class ItemDeleteEvent extends ApplicationEvent {

    @Getter
    private final UUID uuid;

    public ItemDeleteEvent(Object source, UUID uuid) {
        super(source);
        this.uuid = uuid;
    }
}
