package ru.german.service;

import ru.generated.com.cloud.tables.pojos.ElementObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.german.model.ElementPojo;
import ru.german.repository.ElementRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShareService {

    @Autowired
    ElementRepository elementRepository;

    @Value("${amazonPath}")
    String defaultAmazonPath;

    Logger logger = LoggerFactory.getLogger(ShareService.class);

    public ResponseEntity<ElementObject> getActualElement(Long elementId) {
        ElementObject result = elementRepository.getActualElementObjectByEntity(elementId);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<String> getActualContent(Long entityId) {
        Long id = elementRepository.getActualIdByEntity(entityId);
        InputStream inputStream = elementRepository.downloadFile(defaultAmazonPath + id);

        String result = null;
        try {
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            logger.error("Can not find content for element: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<ElementObject> updateActualElement(ElementObject content, Long entityId) {
        ElementObject currentElement = elementRepository.getActualElementObjectByEntity(entityId);
        fillNewFieldsToElement(content, currentElement);

        return ResponseEntity.ok(currentElement);
    }

    private void fillNewFieldsToElement(ElementObject content, ElementObject currentElement) {
        if (content.getStatus() != null) {
            currentElement.setStatus(content.getStatus());
        }
        if (content.getBusinessField() != null) {
            currentElement.setBusinessField(content.getBusinessField());
        }
        //fixme version-db fields can't be change
//        if (content.getIsActual() != null) {
//            currentElement.setIsActual(content.getIsActual());
//        }
//        if (content.getLastUpdateDateTime() != null) {
//            currentElement.setLastUpdateDateTime(content.getLastUpdateDateTime());
//        }
//        if (content.getEntityId() != null) {
//            currentElement.setEntityId(content.getEntityId());
//        }
        //fixme
    }

    public ResponseEntity<String> updateActualContentByElementEntity(String content, Long entityId) {
        Long id = elementRepository.getActualIdByEntity(entityId);
        elementRepository.uploadFile( defaultAmazonPath + id, content);
        return ResponseEntity.ok(content);
    }

    public ResponseEntity<ElementObject> createElementObject(ElementObject content) {
        elementRepository.insert(content);
        return ResponseEntity.ok(content);
    }

    public ResponseEntity<ElementPojo> createElementObjectWithContent(ElementPojo content) {
        Long seqId = elementRepository.getNextValueForElement();
        ElementObject elementObject = content.getElementObject();
        elementObject.setId(seqId);
        elementObject.setEntityId(seqId);
        elementRepository.insert(content.getElementObject());

        elementRepository.uploadFile(defaultAmazonPath + seqId, content.getContent());
        return ResponseEntity.ok(content);
    }

    public ResponseEntity<List<ElementPojo>> getElementTree(Long entityId) {
        List<ElementPojo> result = new ArrayList<>();
        List<ElementObject> elementObjects = elementRepository.getElementTree(entityId);

        elementObjects.forEach(element -> {
            try {
                ElementPojo elementPojo = new ElementPojo();

                InputStream inputStream = elementRepository.downloadFile(defaultAmazonPath + element.getId());
                String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
                elementPojo.setContent(content);
                elementPojo.setElementObject(element);

                result.add(elementPojo);
            } catch (IOException e) {
                logger.error("Can not download content for element: {}", element.getId());
                e.printStackTrace();
            }
        });

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(result);
        }
    }
}
