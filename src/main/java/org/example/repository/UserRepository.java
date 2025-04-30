package org.example.repository;

import lombok.NonNull;
import org.example.repository.entity.Job;
import org.example.repository.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author r0l099q
 */
@Repository
public interface UserRepository
        extends CrudRepository<User, Integer> {

}
