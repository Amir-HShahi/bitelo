package dev.burgerman.bitelo.model.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Invalid Iranian phone number. Must be in format 989XXXXXXXXX (12 digits starting with 98)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowNull() default false;
}

class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final String IRAN_PHONE_PATTERN = "^98\\d{10}$";
    private boolean allowNull;

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null) {
            return allowNull;
        }

        if (phoneNumber.isEmpty()) {
            addCustomMessage(context, "Phone number cannot be empty");
            return false;
        }

        if (!phoneNumber.matches("^\\d+$")) {
            addCustomMessage(context, "Phone number must contain only digits");
            return false;
        }

        if (!phoneNumber.matches(IRAN_PHONE_PATTERN)) {
            addCustomMessage(context, "Phone number must be 12 digits starting with 98 (e.g., 989170627814)");
            return false;
        }

        return true;
    }

    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}