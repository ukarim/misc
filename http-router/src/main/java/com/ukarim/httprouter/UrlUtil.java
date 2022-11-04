package com.ukarim.httprouter;

import java.util.ArrayList;
import java.util.List;

final class UrlUtil {

    private UrlUtil() {}

    static List<String> toPathSegments(String path) {
        int length = path.length();
        int paramsStartIdx = path.indexOf('?');

        // index where query params starts
        int pathEndIdx = paramsStartIdx > 0 ? paramsStartIdx : length;

        List<String> segments = new ArrayList<>();
        boolean previousWasSlash = true;
        int currSegmentStartIdx = 0;

        for (int i = 0; i < pathEndIdx; i++) {
            if ('/' == path.charAt(i)) {
                // if previous was slash and current is slash too, then just ignore
                if (!previousWasSlash) {
                    segments.add(path.substring(currSegmentStartIdx, i));
                }
                previousWasSlash = true;
            } else if (i == (pathEndIdx - 1)) {
                // if path doesn't ends with slash
                segments.add(path.substring(currSegmentStartIdx, i+1));
            } else {
                if (previousWasSlash) {
                    currSegmentStartIdx = i;
                }
                previousWasSlash = false;
            }
        }
        return segments;
    }
}
