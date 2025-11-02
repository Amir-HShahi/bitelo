package dev.burgerman.bitelo.model.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Documented
@Constraint(validatedBy = CountryCodeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CountryCode {
    String message() default "Invalid country code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowNull() default false;

    String[] allowedCodes() default {};

    int minLength() default 1;

    int maxLength() default 3;

    boolean numericOnly() default true;
}

class CountryCodeValidator implements ConstraintValidator<CountryCode, String> {

    private boolean allowNull;
    private Set<String> allowedCodes;
    private int minLength;
    private int maxLength;
    private boolean numericOnly;

    @Override
    public void initialize(CountryCode constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.numericOnly = constraintAnnotation.numericOnly();

        String[] codes = constraintAnnotation.allowedCodes();
        if (codes.length > 0) {
            this.allowedCodes = new HashSet<>(Arrays.asList(codes));
        }
    }

    @Override
    public boolean isValid(String countryCode, ConstraintValidatorContext context) {
        if (countryCode == null) {
            return allowNull;
        }

        if (countryCode.isEmpty()) {
            addCustomMessage(context, "Country code cannot be empty");
            return false;
        }

        // Validate numeric only if required
        if (numericOnly && !countryCode.matches("^\\d+$")) {
            addCustomMessage(context, "Country code must contain only digits");
            return false;
        }

        // Validate length
        if (countryCode.length() < minLength || countryCode.length() > maxLength) {
            addCustomMessage(context,
                    "Country code length must be between " + minLength + " and " + maxLength + " characters");
            return false;
        }

        // Validate against allowed codes if specified
        if (allowedCodes != null && !allowedCodes.isEmpty()) {
            if (!allowedCodes.contains(countryCode)) {
                addCustomMessage(context,
                        "Country code must be one of: " + String.join(", ", allowedCodes));
                return false;
            }
        }

        return true;
    }

    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}