package com.yj2025.reactive.remote;

import com.ecworking.system.remote.agreement.ReceiveAgreementRemote;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("system-setting-api")
public interface ReceiveAgreementClient extends ReceiveAgreementRemote {
}
