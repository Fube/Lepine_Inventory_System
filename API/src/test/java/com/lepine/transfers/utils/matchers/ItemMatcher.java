package com.lepine.transfers.utils.matchers;

import com.lepine.transfers.data.item.Item;
import org.mockito.ArgumentMatcher;

public class ItemMatcher implements ArgumentMatcher<Item> {

    private final Item item;

    public ItemMatcher(Item item) {
        this.item = item;
    }

    @Override
    public boolean matches(Item argument) {
        return argument.getSku().equals(item.getSku()) &&
                argument.getDescription().equals(item.getDescription()) &&
                argument.getName().equals(item.getName());
    }
}
