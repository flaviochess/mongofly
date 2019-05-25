package com.github.mongofly.core.commands.utils;

import com.github.mongofly.core.commands.utils.ConvertCommandBody;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class ConvertCommandBodyTest {

    @Test
    public void shouldSuccess_whenGive_bodyCommandsWithoutInitSpaces_convertTo_equalsDocuments() {

        String bodyCommandWithoutSpaces = "{\"_id\" : {\"$in\": [\"java\", \"kotlin\"]} },{\"$addToSet\": { \"features\" : {\"$each\": [\"jvm\", \"functional\"]} }},{\"multi\": true}";

        List<Document> commandParts = ConvertCommandBody.toDocumentList(bodyCommandWithoutSpaces);

        String jsonBody = commandParts.stream().map(Document::toJson).collect(Collectors.joining(","));

        Assert.assertEquals("body to document", StringUtils.deleteWhitespace(bodyCommandWithoutSpaces), StringUtils.deleteWhitespace(jsonBody));
    }

    @Test
    public void shouldSuccess_whenGive_bodyCommandsWithInitSpaces_convertTo_equalsDocuments() {

        String bodyCommandWithSpaces = "\n{\"_id\" : {\"$in\": [\"java\", \"kotlin\"]} },{\"$addToSet\": { \"features\" : {\"$each\": [\"jvm\", \"functional\"]} }},{\"multi\": true}";

        List<Document> commandParts = ConvertCommandBody.toDocumentList(bodyCommandWithSpaces);

        String jsonBody = commandParts.stream().map(Document::toJson).collect(Collectors.joining(","));

        Assert.assertEquals("body to document", StringUtils.deleteWhitespace(bodyCommandWithSpaces), StringUtils.deleteWhitespace(jsonBody));
    }
}
