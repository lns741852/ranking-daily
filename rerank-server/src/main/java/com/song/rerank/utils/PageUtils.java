package com.song.rerank.utils;


import com.song.rerank.domain.PageResult;
import org.springframework.data.domain.Page;
import java.util.*;

/**
 * 分頁工具
 */
public class PageUtils{

    /**
     * List 分頁
     */
    public static <T> List<T> paging(int page, int size , List<T> list) {
        int fromIndex = page * size;
        int toIndex = page * size + size;
        if(fromIndex > list.size()){
            return Collections.emptyList();
        } else if(toIndex >= list.size()) {
            return list.subList(fromIndex,list.size());
        } else {
            return list.subList(fromIndex,toIndex);
        }
    }

    /**
     * Page 數據處理，預防redis反序列化報錯
     */
    public static <T> PageResult<T> toPage(Page<T> page) {
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    /**
     * 自定義分頁
     */
    public static <T> PageResult<T> toPage(List<T> list, long totalElements) {
        return new PageResult<>(list, totalElements);
    }

    /**
     * 返回空數據
     */
    public static <T> PageResult<T> noData () {
        return new PageResult<>(null, 0);
    }
}