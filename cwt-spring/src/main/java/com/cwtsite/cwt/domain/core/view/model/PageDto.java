package com.cwtsite.cwt.domain.core.view.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PageDto<T> {

    private int size = 10;
    private int start = 1;
    private String sortBy;
    private Boolean sortAscending = false;
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private List<String> sortables;

    public static <T> PageDto<T> toDto(Page<T> pageOfTs, List<String> sortables) {
        final PageDto<T> dto = new PageDto<T>();

        dto.setSize(pageOfTs.getSize());
        dto.setStart(pageOfTs.getNumber());
        dto.setTotalPages(pageOfTs.getTotalPages());
        dto.setTotalElements(pageOfTs.getTotalElements());
        dto.setSortBy(Objects.requireNonNull(pageOfTs.getSort().stream().findFirst().orElse(null)).getProperty());
        dto.setSortAscending(Objects.requireNonNull(pageOfTs.getSort().stream().findFirst().orElse(null)).getDirection().isAscending());
        dto.setContent(pageOfTs.getContent());
        dto.setSortables(sortables);

        return dto;
    }

    private static Sort.Direction mapSortAscendingToDirection(Boolean sortAscending, Sort.Direction fallbackDirection) {
        if (Boolean.TRUE == sortAscending) {
            return Sort.Direction.ASC;
        } else if (Boolean.FALSE == sortAscending) {
            return Sort.Direction.DESC;
        } else {
            return fallbackDirection;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public List<String> getSortables() {
        return sortables;
    }

    public void setSortables(List<String> sortables) {
        this.sortables = sortables;
    }

    public Boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(Boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public Sort asSortWithFallback(Sort.Direction defaultDirection, String defaultSortBy) {
        return Sort.by(
                mapSortAscendingToDirection(sortAscending, defaultDirection),
                Optional.ofNullable(sortBy).orElse(defaultSortBy));
    }
}
