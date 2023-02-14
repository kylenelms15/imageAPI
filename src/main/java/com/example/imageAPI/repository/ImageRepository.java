package com.example.imageAPI.repository;

import com.example.imageAPI.model.ImageDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageDTO, Integer> {

    @Query(value = "SELECT * FROM image WHERE objects LIKE %:object%", nativeQuery = true)
    List<ImageDTO> getImagesByObject(@Param("object") String object);
}
