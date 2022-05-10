package com.yj2025.gateway;

import java.util.Map;
import java.util.Set;

public interface PathMatcherAuthoritiesRemoteLoader {

    /**
     * 获取pathMatcher对应的authorities map集合
     * @return
     */
    Map<String, Set<String>> getPathMatcherAuthoritiesMap();
}
