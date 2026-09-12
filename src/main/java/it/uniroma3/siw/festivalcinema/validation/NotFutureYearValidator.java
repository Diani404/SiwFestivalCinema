package it.uniroma3.siw.festivalcinema.validation;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotFutureYearValidator implements ConstraintValidator<NotFutureYear, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value <= LocalDate.now().getYear();
    }
}
