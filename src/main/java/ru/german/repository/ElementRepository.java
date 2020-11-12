package ru.german.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.generated.com.cloud.Sequences;
import ru.generated.com.cloud.tables.pojos.ElementObject;
import ru.generated.com.cloud.tables.records.ElementObjectRecord;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static ru.generated.com.cloud.tables.ElementObject.ELEMENT_OBJECT;

@Repository
public class ElementRepository {

    @Autowired
    DSLContext dslContext;

    private final AmazonS3 amazonS3;

    @Autowired
    public ElementRepository(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Value("${bucketName}")
    String bucketName;

    public void uploadFile(String fileName, String content) {
        amazonS3.putObject(bucketName, fileName, content);
    }

    public InputStream downloadFile(String keyName) {
        S3Object s3Object = amazonS3.getObject(bucketName, keyName);
        return s3Object.getObjectContent();
    }

    public ElementObject getActualElementObjectByEntity(Long entityId) {
        return dslContext.selectFrom(ELEMENT_OBJECT)
                .where(ELEMENT_OBJECT.ENTITY_ID.eq(entityId),
                        ELEMENT_OBJECT.IS_ACTUAL.isTrue())
                .fetchAnyInto(ElementObject.class);
    }

    public Long getActualIdByEntity(Long entityId) {
        return dslContext.select(ELEMENT_OBJECT.ID).from(ELEMENT_OBJECT)
                .where(ELEMENT_OBJECT.ENTITY_ID.eq(entityId),
                        ELEMENT_OBJECT.IS_ACTUAL.isTrue())
                .fetchAnyInto(Long.class);
    }

    public Long getNextValueForElement() {
        return dslContext.nextval(Sequences.SEQ_ELEMENT_OBJECT_ID);
    }

    public List<ElementObject> getElementTree(Long entityId) {
        List<ElementObject> result = dslContext.selectFrom(ELEMENT_OBJECT)
                .where(ELEMENT_OBJECT.ENTITY_ID.eq(entityId))
                .fetchInto(ElementObject.class);
        return result != null ? result : new ArrayList<ElementObject>();
    }

    public List<ElementObject> getAll() {
        List<ElementObject> result = dslContext.selectFrom(ELEMENT_OBJECT)
                .fetchInto(ElementObject.class);
        return result != null ? result : new ArrayList<>();
    }

    public ElementObject getById(Long id) {
        return dslContext.selectFrom(ELEMENT_OBJECT)
                .where(ELEMENT_OBJECT.ID.eq(id))
                .fetchAnyInto(ElementObject.class);
    }


    public void insertElement(ElementObjectRecord result) {
        dslContext.insertInto(ELEMENT_OBJECT)
                .set(result)
                .execute();
    }

    public void updateElement(ElementObjectRecord currentElement) {
        dslContext.update(ELEMENT_OBJECT)
                .set(currentElement)
                .where(ELEMENT_OBJECT.ID.eq(currentElement.getId()))
                .execute();
    }

    public ElementObjectRecord getActualElementObjectRecordByEntity(Long entityId) {
        return dslContext.selectFrom(ELEMENT_OBJECT)
                .where(ELEMENT_OBJECT.ENTITY_ID.eq(entityId),
                        ELEMENT_OBJECT.IS_ACTUAL.isTrue())
                .fetchAnyInto(ElementObjectRecord.class);
    }
}
