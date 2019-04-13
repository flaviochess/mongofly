package com.github.mongofly.core.converts;

import com.mongodb.DBObject;

import java.util.List;

public interface CommandConvert {

    public static final int DOCUMENTS_LIMIT_SIZE = 1000;

    public static final String ORDERED = "ordered";

    public static final String WRITE_CONVERN = "writeConcern";

    public static final char OPEN_CURLY_BRACES = '{';

    public static final char CLOSE_CURLY_BRACES = '}';

    public List<DBObject> convert(String command);
}
