package com.wrathur.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DebugController {
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    public DebugController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/debug/service-instance/{serviceName}")
    public ServiceInstance getInstance(@PathVariable String serviceName) {
        return loadBalancerClient.choose(serviceName);
    }

    private final DiscoveryClient discoveryClient;

    @GetMapping("/debug/discovery/{serviceName}")
    public List<ServiceInstance> getInstances(@PathVariable String serviceName) {
        return discoveryClient.getInstances(serviceName);
    }
}