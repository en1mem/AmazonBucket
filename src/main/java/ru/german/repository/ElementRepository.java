package ru.german.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.cloud.Sequences;
import com.cloud.tables.daos.ElementObjectDao;
import com.cloud.tables.pojos.ElementObject;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.cloud.tables.ElementObject.ELEMENT_OBJECT;

@Repository
public class ElementRepository extends ElementObjectDao {

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














    public void createRootFolder() {
        amazonS3.createBucket(bucketName);
    }

    public void deleteRootFolder() {
        amazonS3.deleteBucket(bucketName);
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(bucketName, fileName);
    }

    public void makePublic(String fileName) {
        amazonS3.setObjectAcl(bucketName, fileName, CannedAccessControlList.PublicRead);
    }

    public ObjectListing downloadAllFiles() {
        ObjectListing objectListing = amazonS3.listObjects(bucketName);
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {

        }
        return objectListing;
    }

}
