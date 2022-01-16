package com.lepine.transfers.events.item;

import org.springframework.context.event.EventListener;

public interface ItemUpdateHandler {

    @EventListener(ItemUpdateEvent.class)
    void onItemUpdate(ItemUpdateEvent event);
}
