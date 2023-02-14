package com.example.imageAPI.Google;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetectLabels {
    public static List<String> detectLabelsFromBytes(byte[] image) throws IOException {
        ByteString imgBytes = ByteString.copyFrom(image);
        Image img = Image.newBuilder().setContent(imgBytes).build();

        return detectLabels(img);
    }

    public static List<String> detectLabelsFromURI(String uri) throws IOException {
        ImageSource imgSource = ImageSource.newBuilder().setImageUri(uri).build();
        Image img = Image.newBuilder().setSource(imgSource).build();

        return detectLabels(img);
    }

    // Detects labels in the specified local image.
    private static List<String> detectLabels(Image image) throws IOException {
        List<String> imageObjects = new ArrayList<>();
        List<AnnotateImageRequest> requests = new ArrayList<>();

        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION)
                .setMaxResults(5)
                .build();

        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(image).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, res.getError().getMessage());
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    imageObjects.add(annotation.getDescription());
                }
            }
        }

        return imageObjects;
    }
}
