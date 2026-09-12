package it.uniroma3.siw.festivalcinema.validation;

import it.uniroma3.siw.festivalcinema.model.Festival;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPeriodValidator implements ConstraintValidator<ValidPeriod, Festival> {

    @Override
    public boolean isValid(Festival festival, ConstraintValidatorContext context) {
        if (festival.getStartDate() == null || festival.getEndDate() == null) {
            return true;
        }
        return !festival.getEndDate().isBefore(festival.getStartDate());
    }
}
