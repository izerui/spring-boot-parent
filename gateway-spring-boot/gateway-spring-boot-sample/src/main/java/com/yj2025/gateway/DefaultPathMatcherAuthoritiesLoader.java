package com.yj2025.gateway;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class DefaultPathMatcherAuthoritiesLoader implements PathMatcherAuthoritiesRemoteLoader {
    @Override
    public Map<String, Set<String>> getPathMatcherAuthoritiesMap() {
        return Collections.emptyMap();
    }
}
