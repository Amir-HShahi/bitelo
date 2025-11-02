package dev.burgerman.bitelo.model.dto;

import dev.burgerman.bitelo.model.annotation.PhoneNumber;

public record ForgetPasswordRequest(@PhoneNumber String phoneNumber) {
}