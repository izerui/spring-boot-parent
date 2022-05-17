package com.yj2025.gateway.proxy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public interface PathMatcherAuthoritiesLoader {

    /**
     * 获取pathMatcher对应的authorities map集合
     *
     * @return
     */
    Map<String, Set<String>> getPathMatcherAuthoritiesMap();

    PathMatcherAuthoritiesLoader DEFALT = () -> Collections.EMPTY_MAP;
}
