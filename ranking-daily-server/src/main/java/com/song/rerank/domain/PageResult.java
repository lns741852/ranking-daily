package com.song.rerank.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PageResult<T> {

    private final List<T> content;

    private final long totalElements;
}