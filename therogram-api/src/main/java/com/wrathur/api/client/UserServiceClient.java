package com.wrathur.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://user-service")
public interface UserServiceClient {
    @GetMapping("/user/username/{id}")
    String getUsernameById(@PathVariable("id") Integer id);
}