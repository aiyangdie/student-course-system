package com.course.common;

import lombok.Data;

@Data
public class PageQuery {
    private long page = 1;
    private long size = 10;
    private String keyword;
}
