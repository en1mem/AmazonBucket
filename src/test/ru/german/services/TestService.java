package ru.german.services;

import com.amazonaws.AmazonServiceException;
import org.jooq.exception.DataAccessException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.generated.com.cloud.tables.pojos.ElementObject;
import ru.german.model.ContentObject;
import ru.german.repository.ElementRepository;
import ru.german.service.ShareService;

import java.sql.Timestamp;
import java.util.*;


public class TestService extends Assert {

    /**
     * That was a simple spring boot application. All test could be made only for DB or Amazon bucket test.
     * It's useless, bc db or amazon errors can be find in manual tests
     * We need test only business logic
     */

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Autowired
    private ShareService shareService;

    @Autowired
    private ElementRepository repository;

    @Value("${amazonPath}")
    String defaultAmazonPath;

    private List<ElementObject> makeTestElementsObjects() {
        ElementObject elementObject1 = new ElementObject();
        ElementObject elementObject2 = null;
        ElementObject elementObject3 = new ElementObject(-150L, 1L, true, "NEW", new Timestamp(new Date().getTime()), "someStuff");
        ElementObject elementObject4 = new ElementObject(10L, 1L, true, "NEW", new Timestamp(new Date().getTime()), "someStuff");
        ElementObject elementObject5 = new ElementObject();

        return new ArrayList<>(Arrays.asList(elementObject1, elementObject2, elementObject3, elementObject4, elementObject5));
    }

    private List<ContentObject> makeTestContentObjects() {
        ContentObject contentObject1 = new ContentObject(-150L, "testContent", defaultAmazonPath, ".txt");
        ContentObject contentObject2 = new ContentObject(1L, null, defaultAmazonPath, ".txt");
        ContentObject contentObject3 = new ContentObject(1L, "testContent", defaultAmazonPath + "some", ".txt");
        ContentObject contentObject4 = new ContentObject(1L, "testContent", defaultAmazonPath, ".png");
        ContentObject contentObject5 = new ContentObject(1L, "testContent", defaultAmazonPath, ".txt");

        return new ArrayList<>(Arrays.asList(contentObject1, contentObject2, contentObject3, contentObject4, contentObject5));
    }

    private List<ContentObject> makeTestNullContent() {
        ContentObject contentObject6 = null;

        return new ArrayList<>(Collections.singletonList(contentObject6));
    }

    //do not passed caused by spring injection doesnt work
    @Test(expected = DataAccessException.class)
    public void creatingNewElementsWithDbExceptions() {
        List<ElementObject> list = makeTestElementsObjects();
        for (ElementObject elementObject : list) {
            shareService.createElementObject(elementObject);
        }
    }

    //do not passed caused by spring injection doesnt work
    @Test(expected = AmazonServiceException.class)
    public void creatingNewContentWithAmazonException() {
        List<ContentObject> list = makeTestContentObjects();
        for (ContentObject contentObject : list) {
            repository.uploadFile(
                    contentObject.getDefaultPath() +
                            contentObject.getElementId() +
                            contentObject.getPostfix(),
                    contentObject.getContent());
        }
    }

    //passed, but not correct caused by spring injection doesnt work
    @Test(expected = NullPointerException.class)
    public void creatingNewContentWithNPE_Exception() {
        List<ContentObject> nullContentList = makeTestNullContent();

        for (ContentObject  contentObject : nullContentList) {
            assertNull(contentObject);
            repository.uploadFile(
                    contentObject.getDefaultPath() +
                            contentObject.getElementId() +
                            contentObject.getPostfix(),
                    contentObject.getContent());
        }
    }
}
