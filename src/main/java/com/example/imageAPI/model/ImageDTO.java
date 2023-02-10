package com.example.imageAPI.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="image")
public class ImageDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageID;

    @Lob
    @Column(name = "imageData",length = 1000)
    private byte[] imageData;

    private String label;

    private List<String> objects;

    private String imageURI;

}
