package com.example.imageAPI.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseObject {
    private String label;
    private String imageData;
    private Integer id;
    private List<String> objects;
    private String imageURI;
}
