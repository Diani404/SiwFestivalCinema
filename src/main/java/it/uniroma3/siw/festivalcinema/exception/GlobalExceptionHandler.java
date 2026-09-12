package it.uniroma3.siw.festivalcinema.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException e, Model model) {
        model.addAttribute("errorMessage", "Pagina non trovata");
        return "error/404";
    }

    @ExceptionHandler(ReviewNotOwnedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleReviewNotOwned(ReviewNotOwnedException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Errore non gestito", e);
        model.addAttribute("errorMessage", "Si e' verificato un errore interno.");
        return "error/500";
    }
}
