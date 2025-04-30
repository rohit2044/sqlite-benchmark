package org.example.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.util.SimilarItemListConverter;
import org.example.util.StringListConverter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items", indexes = {
        @Index(name = "idx_request_id", columnList = "request_id"),
        @Index(name = "idx_item_id", columnList = "item_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(ItemPK.class)
public class Items {

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Id
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "hierarchy_id", nullable = false)
    private String hierarchyId;

    @Column(name = "hierarchy_type", nullable = false)
    private String hierarchyType;

    // ItemContext embedded objects
    @Embedded
    private ForecastPeriod forecastPeriod;

    @Embedded
    private SalesPeriod salesPeriod;

    @Embedded
    private StoreDetails storeDetails;

    @Column(name = "pricing")
    private String pricing;

    @Column(name = "delivery_speed")
    private String deliverySpeed;

    @Column(name = "similar_items_json")
    @Convert(converter = SimilarItemListConverter.class)
    private List<SimilarItem> similarItems = new ArrayList<>();

    @Column(name = "status")
    private Integer status;

    // Embedded classes
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForecastPeriod {
        @Column(name = "forecast_start_date")
        private Timestamp start;

        @Column(name = "forecast_end_date")
        private Timestamp end;
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesPeriod {
        @Column(name = "sales_start_date")
        private Timestamp start;

        @Column(name = "sales_end_date")
        private Timestamp end;
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreDetails {
        @Column(name = "store_count")
        private Integer storeCount;

        @Column(name = "stores_json")
        @Convert(converter = StringListConverter.class)
        private List<String> stores = new ArrayList<>();
    }

    // SimilarItem class (now as a static nested class, not an entity)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimilarItem {
        private String id;
        private Double weightage;
    }
}