package com.example.imageAPI.service;

import com.example.imageAPI.Google.DetectLabels;
import com.example.imageAPI.model.ImageDTO;
import com.example.imageAPI.model.RequestObject;
import com.example.imageAPI.model.ResponseObject;
import com.example.imageAPI.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageService {


    private final ImageRepository imageRepository;

    private final DetectLabels detectLabels;

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
        }

        if(request.getImageURI() != null && !request.getImageURI().isEmpty()) {
            imageDTO.setImageURI(request.getImageURI());
        }

        if(StringUtils.isEmpty(imageDTO.getImageURI()) && imageDTO.getImageData().length<=0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image or Imamage URI must be provided");
        }

        if(request.isDetectionEnabled()) {
            //code for object detection
            try {
                if (imageDTO.getImageData() != null){
                    imageDTO.setObjects(detectLabels.detectLabelsFromBytes(imageDTO.getImageData()));
                } else {
                    imageDTO.setObjects(detectLabels.detectLabelsFromURI(imageDTO.getImageURI()));
                }

            } catch (IOException e) {
                //something latyer
            }
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

        return convertDTOsToResponse(imageDTOS);
    }

    public ResponseObject getImage(Integer imageID) {
        Optional<ImageDTO> imageDTO = imageRepository.findById(imageID);

        if(imageDTO.isPresent()) {
            return convertDTOtoResponse(imageDTO.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
    }

    public List<ResponseObject> getImageByObjects(List<String> imageObjects) {

        List<ImageDTO> imageDTOS = new ArrayList<>();

        imageObjects.stream().forEach(object -> {
            imageDTOS.addAll(getImagesByObject(object));
        });

        return convertDTOsToResponse(imageDTOS);
    }

    private List<ImageDTO> getImagesByObject(String object)  {
        return imageRepository.getImagesByObject(object);
    }

    private List<ResponseObject> convertDTOsToResponse(List <ImageDTO> imageDTOS) {
        List<ResponseObject> responseObjects = new ArrayList<>();

        imageDTOS.stream().forEach(imageDTO -> {
            responseObjects.add(convertDTOtoResponse(imageDTO));

        });

        return responseObjects;
    }
}
