package com.manage.common.util;

import cn.hutool.core.bean.BeanUtil;

import java.util.Map;

public class ConvertUtils {

    public static Map<String, Object> toMap(Object source) {
        return BeanUtil.beanToMap(source);
    }
}
