package dev.burgerman.bitelo.model.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int minLength() default 8;

    int maxLength() default 128;

    boolean requireUppercase() default true;

    boolean requireLowercase() default true;

    boolean requireDigit() default true;

    boolean requireSpecialChar() default true;

    String specialChars() default "!@#$%^&*()_+-=[]{}|;:,.<>?";
}

class PasswordValidator implements ConstraintValidator<Password, String> {

    private int minLength;
    private int maxLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;
    private String specialChars;

    @Override
    public void initialize(Password constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
        this.specialChars = constraintAnnotation.specialChars();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // Check length
        if (password.length() < minLength || password.length() > maxLength) {
            addCustomMessage(context,
                    "Password length must be between " + minLength + " and " + maxLength + " characters");
            return false;
        }

        // Check uppercase requirement
        if (requireUppercase && !Pattern.compile("[A-Z]").matcher(password).find()) {
            addCustomMessage(context, "Password must contain at least one uppercase letter");
            return false;
        }

        // Check lowercase requirement
        if (requireLowercase && !Pattern.compile("[a-z]").matcher(password).find()) {
            addCustomMessage(context, "Password must contain at least one lowercase letter");
            return false;
        }

        // Check digit requirement
        if (requireDigit && !Pattern.compile("[0-9]").matcher(password).find()) {
            addCustomMessage(context, "Password must contain at least one digit");
            return false;
        }

        // Check special character requirement
        if (requireSpecialChar && !containsSpecialChar(password)) {
            addCustomMessage(context, "Password must contain at least one special character: " + specialChars);
            return false;
        }

        return true;
    }

    private boolean containsSpecialChar(String password) {
        for (char c : password.toCharArray()) {
            if (specialChars.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    private void addCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}