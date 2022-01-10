package com.yj2025.reactive.remote;

import com.ecworking.rbac.remote.UserRemote;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("rbac-api")
public interface UserClient extends UserRemote {
}
