package com.lepine.transfers.http;

import com.lepine.transfers.data.item.ItemMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class Config {
    @Bean
    public ItemMapper getItemMapper() {
        return Mappers.getMapper(ItemMapper.class);
    }
}
