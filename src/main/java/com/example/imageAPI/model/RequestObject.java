package com.example.imageAPI.model;

import lombok.Data;

@Data
public class RequestObject {
    private String image;
    private String label;
    private boolean detectionEnabled;
    private String imageURI;
}
