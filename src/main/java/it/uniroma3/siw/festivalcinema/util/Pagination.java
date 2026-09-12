package it.uniroma3.siw.festivalcinema.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class Pagination {

    public static final int PAGE_SIZE = 10;

    private Pagination() {
    }

    public static Pageable of(Integer page) {
        int number = (page == null || page < 0) ? 0 : page;
        return PageRequest.of(number, PAGE_SIZE);
    }
}
