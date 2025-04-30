package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.JobResponse;
import org.example.dto.RequestType;
import org.example.repository.entity.Job;
import org.example.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PutMapping(path="/job/add")
    public ResponseEntity<JobResponse> addNewUser(@RequestParam RequestType requestType, @RequestParam Integer itemsCounts) {
        long startTime = System.currentTimeMillis();
        long trackingId = jobService.createJobSync(requestType, itemsCounts);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

//        JobResponse response = new JobResponse();
//        response.setStatus("COMPLETED");
//        response.setMessage("Job completed in " + executionTime + " ms");
//        response.setTrackingId(trackingId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/job/delete")
    public void delete(@RequestParam Integer id) {
        jobService.delete(id);
    }

    @GetMapping(path = "/job/getJobs")
    public Iterable<Job> getUser(@RequestParam(required = false) List<Integer> ids) {
        return jobService.getJobFromId(ids);
    }
}