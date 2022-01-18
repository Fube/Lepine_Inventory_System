package com.lepine.transfers.events.item;

import org.springframework.context.event.EventListener;

public interface ItemDeleteHandler {

    @EventListener(ItemDeleteEvent.class)
    void onItemDelete(ItemDeleteEvent event);
}
