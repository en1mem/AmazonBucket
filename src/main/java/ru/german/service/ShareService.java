package ru.german.service;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.generated.com.cloud.tables.pojos.ElementObject;
import ru.generated.com.cloud.tables.records.ElementObjectRecord;
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
            logger.info("Successfully getting actual element, from db");
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<String> getActualContent(Long entityId) {
        Long id = elementRepository.getActualIdByEntity(entityId);
        InputStream inputStream = elementRepository.downloadFile(defaultAmazonPath + id + ".txt");

        String result = null;
        try {
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != null) {
            logger.info("Successfully getting actual content by element, from bucket");
            return ResponseEntity.ok(result);
        } else {
            logger.error("Can not find content for element: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<ElementObject> updateActualElement(ElementObject content, Long entityId) {
        ElementObjectRecord currentElement = elementRepository.getActualElementObjectRecordByEntity(entityId);
        fillNewFieldsToElement(content, currentElement);

        elementRepository.updateElement(currentElement);

        logger.info("Successfully updated actual element");
        return ResponseEntity.ok(currentElement.into(ElementObject.class));
    }

    private void fillNewFieldsToElement(ElementObject content, ElementObjectRecord currentElement) {
        if (content.getStatus() != null) {
            currentElement.setStatus(content.getStatus());
        }
        if (content.getBusinessField() != null) {
            currentElement.setBusinessField(content.getBusinessField());
        }
        //fixme version-db fields can't be changed
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
        elementRepository.uploadFile( defaultAmazonPath + id + ".txt", content);

        logger.info("Successfully updated actual content by element");
        return ResponseEntity.ok(content);
    }

    public ResponseEntity<ElementObject> createElementObject(ElementObject content) {
        elementRepository.insertElement(generateElement(content));

        logger.info("Successfully created element, to db");
        return ResponseEntity.ok(content);
    }

    public ResponseEntity<ElementPojo> createElementObjectWithContent(ElementPojo content) {
        Long seqId = elementRepository.getNextValueForElement();
        ElementObject elementObject = content.getElementObject();
        elementObject.setId(seqId);
        elementObject.setEntityId(seqId);
        elementRepository.insertElement(generateElement(content.getElementObject()));

        elementRepository.uploadFile(defaultAmazonPath + seqId + ".txt", content.getContent());

        logger.info("Successfully created element with content, in db/bucket");
        return ResponseEntity.ok(content);
    }

    public ResponseEntity<List<ElementPojo>> getElementTree(Long entityId) {
        List<ElementPojo> result = new ArrayList<>();
        List<ElementObject> elementObjects = elementRepository.getElementTree(entityId);

        elementObjects.forEach(element -> {
            try {
                ElementPojo elementPojo = new ElementPojo();

                InputStream inputStream = elementRepository.downloadFile(defaultAmazonPath + element.getEntityId() + ".txt");
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
            logger.info("Successfully getting tree of element, from db");
            return ResponseEntity.ok(result);
        }
    }

    public ResponseEntity<String> testConnect(Long entityId) throws IOException {
        InputStream inputStream = elementRepository.downloadFile(defaultAmazonPath + entityId + ".txt");
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        if (content != null) {
            logger.info("Successfully tested");
            return ResponseEntity.ok(content);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    private ElementObjectRecord generateElement(ElementObject content) {
        ElementObjectRecord result = new ElementObjectRecord();

        result.setEntityId(content.getEntityId());
        result.setIsActual(content.getIsActual());
        result.setStatus(content.getStatus());
        result.setLastUpdateDateTime(content.getLastUpdateDateTime());
        result.setBusinessField(content.getBusinessField());

        return result;
    }

    public List<ElementObject> getAllElements() {
        return elementRepository.getAll();
    }

    public ElementObject getById(Long id) {
        return elementRepository.getById(id);
    }
}
