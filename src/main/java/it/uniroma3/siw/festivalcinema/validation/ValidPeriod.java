package it.uniroma3.siw.festivalcinema.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidPeriodValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPeriod {

    String message() default "La data di fine non puo' precedere la data di inizio";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
