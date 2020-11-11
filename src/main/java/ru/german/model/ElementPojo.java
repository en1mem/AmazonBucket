package ru.german.model;

import ru.generated.com.cloud.tables.pojos.ElementObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElementPojo {
    ElementObject elementObject;
    String content;
}
