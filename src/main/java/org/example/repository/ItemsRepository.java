package org.example.repository;

import org.example.repository.entity.ItemPK;
import org.example.repository.entity.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ItemsRepository extends JpaRepository<Items, ItemPK> {
    
    /**
     * Find all items associated with a specific job
     */
    List<Items> findByRequestId(Long requestId);

    /**
     * Find all items associated with multiple jobs
     */
    List<Items> findByRequestIdIn(List<Long> requestIds);
    
    /**
     * Count the number of items for a specific job
     */
    long countByRequestIdIn(List<Long> requestId);

    /**
     * Get total count of items for all specified request IDs
     */
    @Query("SELECT COUNT(*) FROM Items")
    long getCountForAll();

    @Modifying
    @Transactional
    @Query("UPDATE Items i SET i.status = :status WHERE i.requestId IN :requestIds")
    int updateStatusByRequestIds(@Param("requestIds") List<Long> requestIds, @Param("status") Integer status);
}
