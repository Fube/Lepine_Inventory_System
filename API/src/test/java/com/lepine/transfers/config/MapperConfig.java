package com.lepine.transfers.config;

import com.lepine.transfers.data.item.ItemMapper;
import com.lepine.transfers.data.user.UserMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MapperConfig {
    @Bean
    public ItemMapper getItemMapper() {
        return Mappers.getMapper(ItemMapper.class);
    }

    @Bean
    public UserMapper getUserMapper() {
        return Mappers.getMapper(UserMapper.class);
    }
}