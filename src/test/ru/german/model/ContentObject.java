package ru.german.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContentObject {
    Long elementId;
    String content;
    String defaultPath;
    String postfix;
}
