package com.example.imageAPI.service;

import com.example.imageAPI.model.ImageDTO;
import com.example.imageAPI.model.RequestObject;
import com.example.imageAPI.model.ResponseObject;
import com.example.imageAPI.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageService {


    private final ImageRepository imageRepository;

    public ResponseObject submitImage(RequestObject request) {
        ImageDTO imageDTO = convertToDTO(request);
        imageRepository.save(imageDTO);

        return convertDTOtoResponse(imageDTO);
    }

    private ImageDTO convertToDTO(RequestObject request) {
        ImageDTO imageDTO = new ImageDTO();

        if(request.getLabel() != null && !request.getLabel().isEmpty()) {
            imageDTO.setLabel(request.getLabel());
        } else {
            imageDTO.setLabel(generateLabel());
        }

        if(!StringUtils.isEmpty(request.getImage())) {
            byte[] decodedImageData = Base64.getDecoder().decode(request.getImage());
            imageDTO.setImageData(decodedImageData);
            //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image not found.");
        }

        if(request.getImageURI() != null && !request.getImageURI().isEmpty()) {
            imageDTO.setImageURI(request.getImageURI());
        }

        if(imageDTO.getImageURI().isEmpty() && imageDTO.getImageData().length<=0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image or Imamage URI must be provided");
        }

        if(request.isDetectionEnabled()) {
            //code for object detection
        }

        return imageDTO;
    }

    private ResponseObject convertDTOtoResponse(ImageDTO imageDTO) {
        ResponseObject responseObject = new ResponseObject();

        if(imageDTO.getImageData() != null ) {
            String imageData = new String(Base64.getEncoder().encode(imageDTO.getImageData()));
            responseObject.setImageData(imageData);
        }

        responseObject.setObjects(imageDTO.getObjects());
        responseObject.setLabel(imageDTO.getLabel());
        responseObject.setId(imageDTO.getImageID());
        responseObject.setImageURI(imageDTO.getImageURI());

        return responseObject;
    }

    private String generateLabel() {
        return UUID.randomUUID().toString();
    }

    public List<ResponseObject> getAllImages() {
        List<ImageDTO> imageDTOS = imageRepository.findAll();
        List<ResponseObject> responseObjects = new ArrayList<>();

        imageDTOS.stream().forEach(imageDTO -> {
            responseObjects.add(convertDTOtoResponse(imageDTO));

        });

        return responseObjects;
    }

    public ResponseObject getImage(Integer imageID) {
        Optional<ImageDTO> imageDTO = imageRepository.findById(imageID);

        if(imageDTO.isPresent()) {
            return convertDTOtoResponse(imageDTO.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
    }
}
