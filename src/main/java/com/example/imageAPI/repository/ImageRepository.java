package com.example.imageAPI.repository;

import com.example.imageAPI.model.ImageDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageDTO, Integer> {
}
