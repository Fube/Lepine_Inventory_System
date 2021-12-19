package com.lepine.transfers.utils.auth;

public interface JWTUtil<T> {
    String encode(T payload);

    T decode(String tokenValue);
}
