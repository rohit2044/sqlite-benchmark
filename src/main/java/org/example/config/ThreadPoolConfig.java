package org.example.config;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for thread pools and transaction templates
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Value("${app.read.thread.pool.size:3}")
    private int readThreadPoolSize;

    @Value("${app.write.thread.pool.size:1}")
    private int writeThreadPoolSize;

    private ExecutorService readExecutor;
    private ExecutorService writeExecutor;

    /**
     * Creates a thread pool for read operations
     * @return ExecutorService configured for read operations
     */
    @Bean(name = "readExecutor")
    public ExecutorService readExecutor() {
        readExecutor = Executors.newFixedThreadPool(readThreadPoolSize);
        return readExecutor;
    }

    /**
     * Creates a thread pool for write operations
     * @return ExecutorService configured for write operations
     */
    @Bean(name = "writeExecutor")
    public ExecutorService writeExecutor() {
        writeExecutor = Executors.newFixedThreadPool(writeThreadPoolSize);
        return writeExecutor;
    }

    /**
     * Creates a transaction template configured for read-only operations
     * @return TransactionTemplate configured for read-only operations
     */
    @Bean(name = "readOnlyTransactionTemplate")
    public TransactionTemplate readOnlyTransactionTemplate() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setReadOnly(true);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.setTimeout(30); // 30 seconds timeout
        return template;
    }

    /**
     * Creates a transaction template configured for write operations
     * @return TransactionTemplate configured for write operations
     */
    @Bean(name = "writeTransactionTemplate")
    public TransactionTemplate writeTransactionTemplate() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.setTimeout(30); // 30 seconds timeout
        return template;
    }

    /**
     * Shuts down the executor services gracefully
     */
    @PreDestroy
    public void cleanup() {
        shutdownExecutor(readExecutor);
        shutdownExecutor(writeExecutor);
    }

    /**
     * Helper method to shut down an executor service
     * @param executor the executor to shut down
     */
    private void shutdownExecutor(ExecutorService executor) {
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}