package ru.german.controller;

import com.cloud.tables.pojos.ElementObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.german.model.ElementPojo;
import ru.german.service.ShareService;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/manual")
public class MainController {

    @Autowired
    private ShareService service;

    @RequestMapping(value = "/get/actual/{entityId}", method = GET)
    public ResponseEntity<ElementObject> getActualElementObject(@PathVariable Long entityId) {
        return service.getActualElement(entityId);
    }

    @RequestMapping(value = "/get/actual/content/{entityId}", method = GET)
    public ResponseEntity<String> getActualContentByElementEntity(@PathVariable Long entityId) {
        return service.getActualContent(entityId);
    }

    @RequestMapping(value = "/get/element/tree/{entityId}", method = RequestMethod.GET)
    public ResponseEntity<List<ElementPojo>> getElementTree(@PathVariable Long entityId) {
        return service.getElementTree(entityId);
    }

    @Transactional
    @RequestMapping(value = "/update/actual/{entityId}", method = RequestMethod.PUT)
    public ResponseEntity<ElementObject> createElementObject(@RequestBody ElementObject content) {
        return service.createElementObject(content);
    }

    @Transactional
    @RequestMapping(value = "/update/actual/{entityId}", method = RequestMethod.PUT)
    public ResponseEntity<ElementPojo> createElementObjectWithContent(@RequestBody ElementPojo content) {
        return service.createElementObjectWithContent(content);
    }

    @Transactional
    @RequestMapping(value = "/update/actual/{entityId}", method = RequestMethod.PUT)
    public ResponseEntity<ElementObject> updateActualElementByEntity(@RequestBody ElementObject content, @PathVariable Long entityId) {
        return service.updateActualElement(content, entityId);
    }

    @Transactional
    @RequestMapping(value = "/update/actual/content/{entityId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateContentToActualElementEntity(@RequestBody String content, @PathVariable Long entityId) {
        return service.updateActualContentByElementEntity(content, entityId);
    }
}
