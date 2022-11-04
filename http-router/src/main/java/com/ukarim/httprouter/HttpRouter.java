package com.ukarim.httprouter;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class HttpRouter<T> {

    private static final Set<String> KNOWN_HTTP_METHODS = new HashSet<>(Arrays.asList(
            "CONNECT",
            "DELETE",
            "GET",
            "HEAD",
            "OPTIONS",
            "PATCH",
            "POST",
            "PUT",
            "TRACE"
    ));

    private final Map<String, Node<T>> routesByHttpMethod = new HashMap<>();

    private T notFoundHandler;


    public RouterMatch<T> match(String httpMethod, String path) {
        RouterMatch<T> routerMatch = new RouterMatch<>(notFoundHandler);
        Node<T> nodeToInspect = routesByHttpMethod.get(httpMethod);
        if (nodeToInspect == null) {
            return routerMatch;
        }

        List<String> pathSegments = UrlUtil.toPathSegments(path);
        int pathSegmentsCount = pathSegments.size();
        Node<T> matchedNode = null;

        for (int i = 0; i < pathSegmentsCount; i++) {
            String pathSegment = pathSegments.get(i);

            var childNode = nodeToInspect.getChildNode(pathSegment);
            if (childNode != null) {
                nodeToInspect = childNode;
            } else {
                var paramChildNode = nodeToInspect.getParamChildNode();
                if (paramChildNode == null) {
                    // totally unmatched
                    break;
                } else {
                    String variableName = paramChildNode.getName();
                    routerMatch.addPathParam(variableName, pathSegment);
                    nodeToInspect = paramChildNode;
                }
            }

            if (i == (pathSegmentsCount - 1)) {
                matchedNode = nodeToInspect;
            }
        }

        if (matchedNode == null) {
            return routerMatch;
        }

        T handler = matchedNode.getHandler();

        if (handler == null) {
            return routerMatch;
        }
        routerMatch.setHandler(handler);
        return routerMatch;
    }

    public HttpRouter<T> addRoute(String httpMethod, String path, T handler) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with '/'");
        }
        if (!KNOWN_HTTP_METHODS.contains(httpMethod)) {
            throw new IllegalArgumentException("Unknown http method '" + httpMethod + "'");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        Node<T> nodeToInspect = routesByHttpMethod.get(httpMethod); // start from root node
        if (nodeToInspect == null) {
            // create root node if necessary
            nodeToInspect = new Node<>(null);
            routesByHttpMethod.put(httpMethod, nodeToInspect);
        }

        List<String> pathSegments = UrlUtil.toPathSegments(path);
        int pathSegmentsCount = pathSegments.size();
        for (int i = 0; i < pathSegmentsCount; i++) {
            String currentSegment = pathSegments.get(i);
            boolean isParametrizedSegment = currentSegment.startsWith(":");
            String currentSegmentName = isParametrizedSegment ? currentSegment.substring(1) : currentSegment;

            if (isParametrizedSegment) {
                var paramChildNode = nodeToInspect.getParamChildNode();
                if (paramChildNode != null) {
                    if (!paramChildNode.getName().equals(currentSegmentName)) {
                        /* Throw exception for cases like '/users/:login/posts' and '/users/:username/followers' */
                        /* In such cases params must have the same name. For example '/users/:login/posts' and '/users/:login/followers' */
                        String error = String.format("Path variables at the same position must have the same name. Problem with parameters :%s and :%s", currentSegmentName, paramChildNode.getName());
                        throw new IllegalArgumentException(error);
                    }
                    nodeToInspect = paramChildNode;
                } else {
                    var newNode = new Node<T>(currentSegmentName);
                    nodeToInspect.setParamChildNode(newNode);
                    nodeToInspect = newNode;
                }
            } else {
                var childNode = nodeToInspect.getChildNode(currentSegmentName);
                if (childNode != null) {
                    nodeToInspect = childNode;
                } else {
                    var newNode = new Node<T>(currentSegmentName);
                    nodeToInspect.addChildNode(newNode);
                    nodeToInspect = newNode;
                }
            }

            if (i == (pathSegmentsCount - 1)) {
                // if it's latest segment then save the handler
                nodeToInspect.setHandler(handler);
            }
        }

        return this;
    }

    public HttpRouter<T> get(String path, T handler) {
        return addRoute("GET", path, handler);
    }

    public HttpRouter<T> post(String path, T handler) {
        return addRoute("POST", path, handler);
    }

    public HttpRouter<T> put(String path, T handler) {
        return addRoute("PUT", path, handler);
    }

    public HttpRouter<T> delete(String path, T handler) {
        return addRoute("DELETE", path, handler);
    }

    public HttpRouter<T> notFound(T notFoundHandler) {
        this.notFoundHandler = notFoundHandler;
        return this;
    }
}
