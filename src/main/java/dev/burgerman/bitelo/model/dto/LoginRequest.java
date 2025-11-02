package dev.burgerman.bitelo.model.dto;

import dev.burgerman.bitelo.model.annotation.CountryCode;
import dev.burgerman.bitelo.model.annotation.Password;
import dev.burgerman.bitelo.model.annotation.PhoneNumber;

public record LoginRequest(@CountryCode String countryCode, @PhoneNumber String phoneNumber,
        @Password String password) {
}
