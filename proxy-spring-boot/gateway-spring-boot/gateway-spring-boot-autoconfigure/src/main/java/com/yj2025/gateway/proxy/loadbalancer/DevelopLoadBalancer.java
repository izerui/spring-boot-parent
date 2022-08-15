/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yj2025.gateway.proxy.loadbalancer;

import com.yj2025.gateway.proxy.utils.ServerWebExchangeContextHolder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Round-Robin-based implementation of {@link ReactorServiceInstanceLoadBalancer}.
 *
 * @author Spencer Gibb
 * @author Olga Maciaszek-Sharma
 */
public class DevelopLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Log log = LogFactory.getLog(DevelopLoadBalancer.class);

    private final AtomicInteger position;

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    private final String serviceId;

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     *                                            {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId                           id of the service for which to choose an instance
     */
    public DevelopLoadBalancer(
            @NonNull ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
            String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     *                                            {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId                           id of the service for which to choose an instance
     * @param seedPosition                        Round Robin element position marker
     */
    public DevelopLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
            String serviceId, int seedPosition) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.position = new AtomicInteger(seedPosition);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return ServerWebExchangeContextHolder.getExchange()
                .flatMap(serverWebExchange -> {
                    return supplier.get().next().map(serviceInstances -> new RequestServers(serviceInstances, serverWebExchange));
                })
                .map(requestServers -> processInstanceResponse(supplier,
                        requestServers));

//        return supplier.get().next()
//                .map(serviceInstances -> processInstanceResponse(supplier,
//                        serviceInstances));
    }

    private Response<ServiceInstance> processInstanceResponse(
            ServiceInstanceListSupplier supplier,
            RequestServers requestServers) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(requestServers);
        if (supplier instanceof SelectedInstanceCallback
                && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier)
                    .selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(RequestServers requestServers) {

        if (requestServers.serviceInstances.isEmpty()) {
            log.warn("No servers available for service: " + this.serviceId);
            return new EmptyResponse();
        }
        if (requestServers.exchange != null) {
            String pcServer = requestServers.exchange.getRequest().getHeaders().getFirst("pcServer");
            if (pcServer != null && !"".equals(pcServer)) {
                ServiceInstance instance = requestServers.serviceInstances.stream().filter(serviceInstance -> pcServer.contains(serviceInstance.getHost())).findFirst().orElse(null);
                if (instance != null) {
                    return new DefaultResponse(instance);
                }
            }
        }
        // TODO: enforce order?
        int pos = Math.abs(this.position.incrementAndGet());

        ServiceInstance instance = requestServers.serviceInstances.get(pos % requestServers.serviceInstances.size());

        return new DefaultResponse(instance);
    }

    @AllArgsConstructor
    private class RequestServers {
        private List<ServiceInstance> serviceInstances;
        private ServerWebExchange exchange;
    }

}
