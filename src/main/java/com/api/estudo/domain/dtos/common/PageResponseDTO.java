package com.api.estudo.domain.dtos.common;

import java.util.List;

public record PageResponseDTO<T>(
        List<T> content,
        int page,
        int totalPerPage,
        int totalPages,
        long totalElements
    ) {

}
