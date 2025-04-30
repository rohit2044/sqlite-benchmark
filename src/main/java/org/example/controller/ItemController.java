package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.repository.entity.Items;
import org.example.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(path = "/items/getJobs")
    public Iterable<Items> getItemsForJobId(@RequestParam(required = false) List<Long> jobId) {
        return itemService.getJobsFromIdMultiThreaded(jobId);
    }

    @GetMapping(path = "/items/count")
    public Long getCountForJobId(@RequestParam(required = false) List<Long> jobId) {
        return itemService.getCountForJobId(jobId);
    }

    @PutMapping(path = "/job/updateJobId")
    public String updateJobId(@RequestParam Long jobId) {
        int updatedCount = itemService.update(jobId, 2);
        return "Updated " + updatedCount + " items";
    }

    @Deprecated
    @PutMapping(path = "/job/update")
    public String updateJob() {
        int updatedCount = itemService.updateAll(2);
        return "Updated " + updatedCount + " items";
    }
}