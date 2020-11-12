package ru.german.model;

import lombok.Getter;
import lombok.Setter;
import ru.generated.com.cloud.tables.pojos.ElementObject;

@Getter
@Setter
public class ElementPojo {
    ElementObject elementObject;
    String content;
}
