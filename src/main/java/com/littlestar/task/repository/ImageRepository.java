package com.littlestar.task.repository;

import com.littlestar.task.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Object> findByImageUrl(String url);
}
