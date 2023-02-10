package com.example.imageAPI.controller;

import com.example.imageAPI.model.RequestObject;
import com.example.imageAPI.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/imageAPI")
public class ImageController {

    private final ImageService imageService;

    @PostMapping(path = "/images", consumes = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity addImage(@RequestBody RequestObject request) {

        return ResponseEntity.ok(imageService.submitImage(request));
    }

    @GetMapping(path = "/images")
    public @ResponseBody ResponseEntity getImages() {

        return ResponseEntity.ok(imageService.getAllImages());
    }

    @GetMapping(path="/images/{imageID}")
    public @ResponseBody ResponseEntity getImage(@PathVariable Integer imageID) {

        return ResponseEntity.ok(imageService.getImage(imageID));
    }
}
