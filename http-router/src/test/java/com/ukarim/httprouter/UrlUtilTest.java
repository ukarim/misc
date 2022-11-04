package com.ukarim.httprouter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class UrlUtilTest {

    @Test
    void check() {
        List<String> expectedSegments = Arrays.asList("users", "ukarim", "comments");
        List<String> actualSegments = UrlUtil.toPathSegments("/users/ukarim/comments");

        Assertions.assertEquals(expectedSegments.size(), actualSegments.size());

        for (int i = 0; i < expectedSegments.size(); i++) {
            Assertions.assertEquals(expectedSegments.get(i), actualSegments.get(i));
        }
    }
}
