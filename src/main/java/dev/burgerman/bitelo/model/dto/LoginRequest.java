package dev.burgerman.bitelo.model.dto;

public record LoginRequest(String countryCode, String phoneNumber, String password) {
}
