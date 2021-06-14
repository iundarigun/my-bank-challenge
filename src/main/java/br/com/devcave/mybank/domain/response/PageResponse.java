package br.com.devcave.mybank.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> implements Serializable {
    private final Integer currentPage;

    private final Integer totalPages;

    private final Long totalElements;

    private final List<T> content;
}
