package it.uniroma3.siw.festivalcinema.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class GlobalBindingConfig {

    //x duplicate problems e null
    @InitBinder
    public void initBinder(WebDataBinder binder) {
         binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
