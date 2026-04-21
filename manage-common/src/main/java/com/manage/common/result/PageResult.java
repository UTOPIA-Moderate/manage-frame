package com.manage.common.result;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private int page;
    private int pageSize;
    private long total;
    private List<T> list;

    public static <T> PageResult<T> of(int page, int pageSize, long total, List<T> list) {
        PageResult<T> r = new PageResult<>();
        r.setPage(page);
        r.setPageSize(pageSize);
        r.setTotal(total);
        r.setList(list);
        return r;
    }
}
