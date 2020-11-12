package ru.german.services;

import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.generated.com.cloud.tables.pojos.ElementObject;
import ru.german.model.ContentObject;
import ru.german.service.ShareService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class TestService {
    Logger logger = LoggerFactory.getLogger(TestService.class);

    @Autowired
    private ShareService shareService;

    String defaultPath = "com.german.";

    @Before
    public void setUp() {
        logger.info("Code executes before each test method");

        ElementObject elementObject1 = new ElementObject();
        ElementObject elementObject2 = null;
        ElementObject elementObject3 = new ElementObject(-150L, 1L, true, "NEW", new Timestamp(new Date().getTime()), "someStuff");
        ElementObject elementObject4 = new ElementObject(10L, 1L, true, "NEW", new Timestamp(new Date().getTime()), "someStuff");
        ElementObject elementObject5 = new ElementObject();
        ElementObject elementObject6 = new ElementObject();
        List<ElementObject> list = new ArrayList<>(Arrays.asList(elementObject1, elementObject2, elementObject3, elementObject4, elementObject5, elementObject6));


        ContentObject contentObject = new ContentObject(-150L, "testContent");
        ContentObject contentObject1 = new ContentObject();
        ContentObject contentObject2 = new ContentObject();
        ContentObject contentObject3 = new ContentObject();


        creatingNewUsers(list);
    }

    public void creatingNewUsers(List<ElementObject> list) {
        for (ElementObject elementObject : list) {
            //Assert.assertThat(elementObject.getId());
            shareService.createElementObject(elementObject);
            //Assert.assertThat();
        }
    }

    public void creatingNewUsersWithContent(List<ElementObject> list) {
        for (ElementObject elementObject : list) {
            //Assert.assertThat(elementObject.getId());
            shareService.createElementObject(elementObject);
            //Assert.assertThat();
        }
    }

    public void updatingNewUsers(List<ElementObject> list) {
        for (ElementObject elementObject : list) {
            //Assert.assertThat(elementObject.getId());
            shareService.createElementObject(elementObject);
            //Assert.assertThat();
        }
    }

    public void updatingNewUsersWithContent(List<ElementObject> list) {
        for (ElementObject elementObject : list) {
            //Assert.assertThat(elementObject.getId());
            shareService.createElementObject(elementObject);
            //Assert.assertThat();
        }
    }

    public void updatingContent(String content) {

    }

    public void insertingContent(String content) {

    }
}
