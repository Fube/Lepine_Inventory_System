package com.lepine.transfers.events.item;

import com.lepine.transfers.data.item.Item;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ItemUpdateEvent extends ApplicationEvent {
    @Getter
    private final Item item;

    public ItemUpdateEvent(Object source, Item item) {
        super(source);
        this.item = item;
    }
}
