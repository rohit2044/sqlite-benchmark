package org.example.util;

import lombok.experimental.UtilityClass;
import org.example.repository.entity.Items;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class ItemGenerator {
    
    /**
     * Create the specified number of items linked to a job
     */
    public List<Items> createItems(int count, Long jobId) {
        List<Items> result = new ArrayList<>(count);
        
        for (int i = 1; i <= count; i++) {
            Items item = createRandomItem(jobId, (long) i);
            result.add(item);
        }

        return result;
    }
    
    /**
     * Create a random item with realistic data
     */
    private Items createRandomItem(Long jobId, Long itemId) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // Create forecast and sales periods
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime forecastStart = now.plusDays(random.nextInt(1, 10));
        LocalDateTime forecastEnd = forecastStart.plusDays(random.nextInt(30, 90));
        LocalDateTime salesStart = forecastStart.plusDays(random.nextInt(5, 15));
        LocalDateTime salesEnd = salesStart.plusDays(random.nextInt(30, 60));
        
        // Create store details
        int storeCount = random.nextInt(1, 10);
        List<String> stores = new ArrayList<>();
        for (int i = 0; i < storeCount; i++) {
            stores.add("store_" + random.nextInt(1, 1000));
        }
        
        // Create similar items
        int similarItemCount = random.nextInt(0, 5);
        List<Items.SimilarItem> similarItems = new ArrayList<>();
        for (int i = 0; i < similarItemCount; i++) {
            Items.SimilarItem similarItem = new Items.SimilarItem(
                    String.valueOf(random.nextInt(10000, 99999)),
                    random.nextDouble(0.1, 1.0)
            );
            similarItems.add(similarItem);
        }
        
        // Create the item
        return Items.builder()
                .itemId(itemId)
                .hierarchyId(String.valueOf(10000 + random.nextInt(90000)))
                .hierarchyType("wpid")
                .forecastPeriod(new Items.ForecastPeriod(
                        Timestamp.from(forecastStart.toInstant(ZoneOffset.UTC)),
                        Timestamp.from(forecastEnd.toInstant(ZoneOffset.UTC))
                ))
                .salesPeriod(new Items.SalesPeriod(
                        Timestamp.from(salesStart.toInstant(ZoneOffset.UTC)),
                        Timestamp.from(salesEnd.toInstant(ZoneOffset.UTC))
                ))
                .storeDetails(new Items.StoreDetails(
                        storeCount,
                        stores
                ))
                .pricing(String.valueOf(random.nextDouble(10.0, 500.0)))
                .deliverySpeed(getRandomDeliverySpeed())
                .similarItems(similarItems)
                .status(1) // Assuming 1 is "ACTIVE" or similar
                .requestId(jobId)
                .build();
    }
    
    private String getRandomDeliverySpeed() {
        List<String> options = Arrays.asList("ONE_DAY", "TWO_DAY", "STANDARD", "EXPRESS");
        return options.get(ThreadLocalRandom.current().nextInt(options.size()));
    }
}