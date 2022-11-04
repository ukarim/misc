package com.ukarim.httprouter;

import java.util.HashMap;
import java.util.Map;

final class Node<T> {

    private final String name;

    private final Map<String, Node<T>> childNodes = new HashMap<>();

    private Node<T> paramChildNode;

    private T handler;

    Node(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    Node<T> getChildNode(String name) {
        return childNodes.get(name);
    }

    void addChildNode(Node<T> node) {
        childNodes.put(node.name, node);
    }

    Node<T> getParamChildNode() {
        return paramChildNode;
    }

    void setParamChildNode(Node<T> paramChildNode) {
        this.paramChildNode = paramChildNode;
    }

    T getHandler() {
        return handler;
    }

    void setHandler(T handler) {
        this.handler = handler;
    }
}
