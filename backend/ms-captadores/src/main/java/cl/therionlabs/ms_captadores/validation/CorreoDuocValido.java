package cl.therionlabs.ms_captadores.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CorreoDuocValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CorreoDuocValido {

    String message() default "Debe ingresar un correo institucional de Duoc UC";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}