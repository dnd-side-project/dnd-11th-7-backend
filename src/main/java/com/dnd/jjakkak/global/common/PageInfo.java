package com.dnd.jjakkak.global.common;

import lombok.Builder;
import lombok.Getter;

/**
 * 페이지 정보를 담는 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 27.
 */
@Getter
public class PageInfo {

    private final int page;
    private final int size;
    private final int totalElements;
    private final int totalPages;

    @Builder
    public PageInfo(int page, int size, int totalElements, int totalPages) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
