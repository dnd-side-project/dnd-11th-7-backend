package com.dnd.tikitaka.domain.example.repository;

import com.dnd.tikitaka.domain.example.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Example Repository Class.
 *
 * @author 정승조
 * @version 2024. 07. 17.
 */
public interface ExampleRepository extends JpaRepository<Example, Long> {


}
