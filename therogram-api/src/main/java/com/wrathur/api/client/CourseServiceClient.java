package com.wrathur.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "course-service", url = "http://course-service")
public interface CourseServiceClient {
    @GetMapping("/course/student/{id}")
    List<Integer> getStudentIdsByCourseId(@PathVariable("id") Integer id);
}