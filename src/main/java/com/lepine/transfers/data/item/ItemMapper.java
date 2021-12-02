package com.lepine.transfers.data.item;

public interface ItemMapper {

    Item toEntity(ItemUUIDLessDTO itemDTO);
}
