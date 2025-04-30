package org.example.repository;

import lombok.NonNull;
import org.example.repository.entity.Job;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author j0k0anj
 */
@Repository
public interface JobRepository
    extends CrudRepository<Job, Integer> {

    @Query("SELECT j.id FROM Job j")
    List<Long> findAllJobIds();

}
