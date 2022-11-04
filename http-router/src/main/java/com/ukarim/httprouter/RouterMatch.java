package com.ukarim.httprouter;

public class RouterMatch<T> {

    private T handler;

    private PathParams pathParams;

    RouterMatch(T handler) {
        this.handler = handler;
    }

    public PathParams getPathParams() {
        return pathParams != null ? pathParams : PathParams.EMPTY;
    }

    public T getHandler() {
        return handler;
    }

    void addPathParam(String name, String value) {
        if (pathParams == null) {
            pathParams = new PathParams();
        }
        pathParams.addParam(name, value);
    }

    void setHandler(T handler) {
        this.handler = handler;
    }
}
