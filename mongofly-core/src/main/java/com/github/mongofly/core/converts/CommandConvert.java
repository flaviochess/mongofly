package com.github.mongofly.core.converts;

import org.bson.Document;

import java.util.List;

public interface CommandConvert {

    int DOCUMENTS_LIMIT_SIZE = 1000;

    String ORDERED = "ordered";

    String WRITE_CONCERN = "writeConcern";

    String MULTI = "multi";

    char CURLY_BRACES_OPEN = '{';

    char CURLY_BRACES_CLOSE = '}';

    List<Document> convert(String command);
}
