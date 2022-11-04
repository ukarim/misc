package com.ukarim.httprouter;

import java.util.HashMap;
import java.util.Map;

public final class PathParams {

    public static final PathParams EMPTY = new PathParams();

    private Map<String, String> params;

    PathParams() {}

    void addParam(String name, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(name, value);
    }

    public String getParam(String name) {
        if (params == null) {
            return null;
        }
        return params.get(name);
    }
}
