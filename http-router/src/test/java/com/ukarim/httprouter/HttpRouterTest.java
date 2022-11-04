package com.ukarim.httprouter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpRouterTest {

    @Test
    void shouldFailForUnknownMethod() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("WRONG_HTTP_METHOD", "/users", "TEST_HANDLER");
        });
    }

    @Test
    void shouldFailForNullHandler() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("GET", "/users", null);
        });
    }

    @Test
    void shouldFailForNonAbsolutePaths() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("GET", "users", "TEST_HANDLER");
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("GET", "", "TEST_HANDLER");
        });
    }

    @Test
    void shouldFailForNullPath() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpRouter<String>().addRoute("GET", null, "TEST_HANDLER");
        });
    }

    @Test
    void checkRouter() {
        var handler = "test_handler";
        var httpRouter = new HttpRouter<String>();
        httpRouter.addRoute("GET", "/users/:login/posts", handler);
        httpRouter.addRoute("POST", "/users/new", handler);
        httpRouter.addRoute("GET", "/users/:login/comments/:comment_id", handler);

        RouterMatch<String> routerMatchGet = httpRouter.match("GET", "/users/ukarim/posts");
        Assertions.assertNotNull(routerMatchGet);
        Assertions.assertEquals(handler, routerMatchGet.getHandler());
        Assertions.assertEquals("ukarim", routerMatchGet.getPathParams().getParam("login"));

        RouterMatch<String> routerMatchPost = httpRouter.match("POST", "/users/new");
        Assertions.assertNotNull(routerMatchPost);
        Assertions.assertEquals(handler, routerMatchPost.getHandler());

        RouterMatch<String> routerMatch2Param = httpRouter.match("GET", "/users/ukarim/comments/1024");
        Assertions.assertNotNull(routerMatch2Param);
        Assertions.assertEquals(handler, routerMatch2Param.getHandler());
        Assertions.assertEquals("ukarim", routerMatch2Param.getPathParams().getParam("login"));
        Assertions.assertEquals("1024", routerMatch2Param.getPathParams().getParam("comment_id"));
    }

    @Test
    void checkNotFound() {
        var handler = "test_handler";
        var notFoundHandler = "not_found_handler";
        var httpRouter = new HttpRouter<String>();
        httpRouter.notFound(notFoundHandler);

        // empty router test
        RouterMatch<String> notFoundMatch1 = httpRouter.match("PUT", "/random/url1");
        Assertions.assertEquals(notFoundHandler, notFoundMatch1.getHandler());

        httpRouter.put("/help/1", handler);
        RouterMatch<String> routerMatch1 = httpRouter.match("PUT", "/help/1");
        Assertions.assertEquals(handler, routerMatch1.getHandler());

        // after adding one handler
        RouterMatch<String> notFoundMatch2 = httpRouter.match("PUT", "/random/url2");
        Assertions.assertEquals(notFoundHandler, notFoundMatch2.getHandler());
    }

}
