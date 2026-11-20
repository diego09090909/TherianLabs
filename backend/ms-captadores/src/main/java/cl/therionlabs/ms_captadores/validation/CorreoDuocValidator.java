package cl.therionlabs.ms_captadores.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CorreoDuocValidator implements ConstraintValidator<CorreoDuocValido, String> {

    @Override
    public boolean isValid(String correo, ConstraintValidatorContext context) {
        if (correo == null || correo.isBlank()) {
            return true;
        }

        String limpio = correo.trim().toLowerCase();
        return limpio.matches("^[a-z0-9._%+-]+@(duocuc\\.cl|duoc\\.cl|profesor\\.duoc\\.cl)$");
    }
}