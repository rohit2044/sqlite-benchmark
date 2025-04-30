package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.repository.ItemsRepository;
import org.example.repository.JobRepository;
import org.example.repository.entity.Items;
import org.example.repository.entity.Job;
import org.example.util.ItemGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemsRepository itemsRepository;
    private final JobRepository jobRepository;
    @Autowired
    private ExecutorService readExecutor;

    @Autowired
    private ExecutorService writeExecutor;

    @Autowired
    private TransactionTemplate readOnlyTransactionTemplate;

    @Autowired
    private TransactionTemplate writeTransactionTemplate;


    @Transactional(readOnly = true)
    public Iterable<Items> getJobFromId(List<Long> ids) {

        CompletableFuture<Iterable<Items>> future = CompletableFuture.supplyAsync(() ->
        {
            if (CollectionUtils.isEmpty(ids)) return itemsRepository.findAll();
            return itemsRepository.findByRequestIdIn(ids);
        }, readExecutor);
        // Wait for completion - this makes the HTTP call wait
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update job", e);
        }
    }



    /**
     * Multi-threaded implementation to get items by IDs
     * Each thread gets its own database connection from the pool
     */
    public List<Items> getJobsFromIdMultiThreaded(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return readOnlyTransactionTemplate.execute(status ->
                    StreamSupport.stream(itemsRepository.findAll().spliterator(), false)
                            .collect(Collectors.toList())
            );
        }

        // Calculate optimal batch size based on available threads
        int availableThreads = Math.min(ids.size(), readExecutor.hashCode() % 100); // This is a hack to get thread pool size
        int batchSize = Math.max(1, ids.size() / availableThreads);
        List<CompletableFuture<List<Items>>> futures = new ArrayList<>();

        // Create a future for each batch
        for (int i = 0; i < ids.size(); i += batchSize) {
            final int start = i;
            final int end = Math.min(i + batchSize, ids.size());

            CompletableFuture<List<Items>> future = CompletableFuture.supplyAsync(() -> {
                // Create a new transaction in this thread
                return readOnlyTransactionTemplate.execute(status -> {
                    List<Long> batchIds = ids.subList(start, end);
                    Iterable<Items> items = itemsRepository.findByRequestIdIn(batchIds);
                    return StreamSupport.stream(items.spliterator(), false)
                            .collect(Collectors.toList());
                });
            }, readExecutor);

            futures.add(future);
        }

        try {
            // Wait for all futures to complete
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));

            // Collect and merge all results
            return allFutures.thenApply(v ->
                    futures.stream()
                            .map(CompletableFuture::join)
                            .flatMap(List::stream)
                            .collect(Collectors.toList())
            ).get(30, TimeUnit.SECONDS); // Add reasonable timeout

        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve jobs", e);
        }
    }




    public Long getCountForJobId(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) return itemsRepository.getCountForAll();
        return itemsRepository.countByRequestIdIn(ids);
    }

    public int update(Long jobId, Integer status) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() ->
                itemsRepository.updateStatusByRequestIds(List.of(jobId), status), writeExecutor);
        // Wait for completion - this makes the HTTP call wait
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update job", e);
        }
    }

    public int updateAll(Integer status) {
        List<Long> jobIds = jobRepository.findAllJobIds();
        return itemsRepository.updateStatusByRequestIds(jobIds, status);
    }
}