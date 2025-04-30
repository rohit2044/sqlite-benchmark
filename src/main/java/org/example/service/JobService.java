package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.RequestType;
import org.example.repository.ItemsRepository;
import org.example.repository.JobRepository;
import org.example.repository.entity.Items;
import org.example.repository.entity.Job;
import org.example.util.ItemGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final ItemsRepository itemsRepository;

    @Autowired
    private ExecutorService writeExecutor;

    @Autowired
    private TransactionTemplate writeTransactionTemplate;


    public Job createJobForSave(RequestType requestType) {
        Job job = new Job();

        Calendar cal = Calendar.getInstance();

        // Subtract 3 days from the current date and time
        cal.add(Calendar.DATE, -3);
        Timestamp threeDaysAgo = new Timestamp(cal.getTimeInMillis());

        // Subtract 1 days from the current date and time
        cal.add(Calendar.DATE, -1);
        Timestamp oneDaysAgo = new Timestamp(cal.getTimeInMillis());

        // Add 5 days to the current date and time
        cal.add(Calendar.DATE, 5);
        Timestamp fiveDaysLater = new Timestamp(cal.getTimeInMillis());

        job.setStatus(1);
        job.setCreatedTs(threeDaysAgo);
        job.setModifiedAt(oneDaysAgo);
        job.setCompletionTs(new Timestamp(System.currentTimeMillis()));
        job.setMetadata("This is a dummy metadata");
        job.setConsumerId(UUID.randomUUID());
        job.setRequestType(requestType); // Replace YOUR_ENUM_VALUE with the actual enum value
        job.setCreatedBy("Dummy creator");
        job.setLastModifiedUser("Dummy modifier");
        job.setLastModifiedProcess("Dummy process");
        job.setEstCompletionTs(fiveDaysLater);
        job.setInput("Dummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy inputDummy input");
        job.setOutput("Dummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy outputDummy output");
        return job;
    }

    public Iterable<Job> getJobFromId(List<Integer> ids) {
        if(CollectionUtils.isEmpty(ids)) return jobRepository.findAll();
        return jobRepository.findAllById(ids);
    }

    public void delete(Integer trackingId) {
        if(getJobFromId(List.of(trackingId)).iterator().hasNext()) {
            jobRepository.deleteById(trackingId);
        } else {
            throw new NoSuchElementException("No jobs to delete for id " + trackingId);
        }
    }

        /**
     * Synchronous method for benchmarking using proper transaction template
     * @param requestType The type of request
     * @param itemsToCreate Number of items to create
     * @return The tracking ID of the created job
     */
    public Long createJobSync(RequestType requestType, Integer itemsToCreate) {
        // Use CompletableFuture with writeExecutor to handle the write operation
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            // Use writeTransactionTemplate to manage the transaction properly
            return writeTransactionTemplate.execute(status -> {
                try {
                    // Create and save the job within the transaction
                    Job job = createJobForSave(requestType);
                    Job savedJob = jobRepository.save(job);

                    // Create and save the items within the same transaction
                    List<Items> itemsList = ItemGenerator.createItems(itemsToCreate, savedJob.getTrackingId());
                    itemsRepository.saveAll(itemsList);

                    return savedJob.getTrackingId();
                } catch (Exception e) {
                    // Mark the transaction for rollback
                    status.setRollbackOnly();
                    throw new RuntimeException("Database operation failed", e);
                }
            });
        }, writeExecutor);

        // Wait for completion - this makes the HTTP call wait
        try {
            return future.get(30, TimeUnit.SECONDS); // Add timeout to prevent hanging
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to create job: " + e.getCause().getMessage(), e.getCause());
        } catch (TimeoutException e) {
            throw new RuntimeException("Operation timed out after 30 seconds", e);
        }
    }

//    // Single annotation routes this method to our executor
//    @Async("sqliteWriteExecutor")
//    public CompletableFuture<Long> createJob(RequestType requestType, Integer itemsToCreate) {
//        try {
//            // All operations below will execute sequentially on a single thread
//            Job job = createJobForSave(requestType);
//            Job savedJob = jobRepository.save(job);
//
//            List<Items> itemsList = ItemGenerator.createItems(itemsToCreate, savedJob.getTrackingId());
//            itemsRepository.saveAll(itemsList);
//
//            // Return tracking ID for client to use
//            return CompletableFuture.completedFuture(savedJob.getTrackingId());
//        }  catch (Exception e) {
//            // Log error instead of returning a failed future
//            log.error("Error creating job: {}", e.getMessage());
//            // Return a completed future with null value
//            return CompletableFuture.completedFuture(null);
//        }
//    }
}