package com.football.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public class PagedResponse<T> {
    public List<T> content;
    public int page;
    public int totalPages;
    public long totalElements;
    public boolean first;
    public boolean last;

    public static <T> PagedResponse<T> of(Page<T> p) {
        PagedResponse<T> r = new PagedResponse<>();
        r.content = p.getContent();
        r.page = p.getNumber();
        r.totalPages = p.getTotalPages();
        r.totalElements = p.getTotalElements();
        r.first = p.isFirst();
        r.last = p.isLast();
        return r;
    }
}
