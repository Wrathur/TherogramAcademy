package com.wrathur.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "instruction-service", url = "http://localhost:8083/api")
public interface InstructionServiceClient {
    @GetMapping("/courseResource/courseResources/{id}")
    List<Integer> getCourseResourceIdsByCourseId(@PathVariable Integer id);

    @GetMapping("/homework/homeworks/{id}")
    List<Integer> getHomeworkIdsByCourseId(@PathVariable Integer id);

    @PostMapping("/homework/homeworks")
    List<Integer> getHomeworkIdsByCourseIds(@RequestBody List<Integer> ids);
}