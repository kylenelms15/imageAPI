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

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {


    private final ImageRepository imageRepository;

    public ResponseObject submitImage(RequestObject request) {
        ImageDTO imageDTO = convertToDTO(request);
        imageRepository.save(imageDTO);

        return convertToResponseObject(imageDTO, request.getImage());
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

    private ResponseObject convertToResponseObject(ImageDTO imageDTO, String encodedImage) {
        ResponseObject responseObject = new ResponseObject();

        responseObject.setImageData(encodedImage);
        responseObject.setObjects(imageDTO.getObjects());
        responseObject.setLabel(imageDTO.getLabel());
        responseObject.setId(imageDTO.getImageID());
        responseObject.setImageURI(imageDTO.getImageURI());

        return responseObject;
    }

    private String generateLabel() {
        return UUID.randomUUID().toString();
    }
}
