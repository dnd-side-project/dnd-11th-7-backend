package com.dnd.jjakkak.global.common;

import lombok.Builder;
import lombok.Getter;

/**
 * 페이징 처리된 응답을 담는 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 27.
 */
@Getter
public class PagedResponse<T> {

    private final T data;
    private final PageInfo pageInfo;

    @Builder
    public PagedResponse(T data, PageInfo pageInfo) {
        this.data = data;
        this.pageInfo = pageInfo;
    }
}
