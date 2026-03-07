package com.wrathur.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "course-service", url = "http://localhost:8082")
public interface CourseServiceClient {
    @GetMapping("/course/student/{id}")
    List<Integer> getStudentIdsByCourseId(@PathVariable Integer id);
}