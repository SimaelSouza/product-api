package com.example.products.dtos;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponseDto<T>(
        List<T> data,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public PageResponseDto (Page<T> pageData ){
        this(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages()
        );
    }
}
