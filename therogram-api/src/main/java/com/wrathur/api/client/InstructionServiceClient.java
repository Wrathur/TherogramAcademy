package com.wrathur.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "instruction-service", url = "http://instruction-service")
public interface InstructionServiceClient {
    @GetMapping("/homework/course/{id}")
    List<Integer> getHomeworkIdsByCourseId(@PathVariable("id") Integer id);
}