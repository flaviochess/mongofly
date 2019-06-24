package com.github.mongofly.core.commands.createindex;

import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Assert;
import org.junit.Test;

public class CreateIndexConvertTest {

    @Test
    public void shouldSuccess_whenGive_createIndexCommand_extractTheCommand() {

        String command = "db.city.createIndex({name: \"text\"}, {default_language: \"portuguese\"})";

        CreateIndexObject createIndexObject = CreateIndexConvert.convert(command);

        Document keysExpected = Document.parse("{name: \"text\"}");
        Assert.assertEquals(keysExpected, createIndexObject.getKeys());
    }

    @Test
    public void shouldSuccess_whenGive_createIndexCommand_extractOptionsAndConvertToIndexOptions() {

        String command = "db.city.createIndex({name: \"text\"}, {background: true, default_language: \"portuguese\", " +
                "partialFilterExpression: {name: {$exists: true}}, expireAfterSeconds: 50, " +
                "collation: {locale: \"simple\", caseLevel: true, strength: 1} })";

        CreateIndexObject createIndexObject = CreateIndexConvert.convert(command);

        Assert.assertTrue(createIndexObject.getIndexOptions().isPresent());

        IndexOptions indexOptions = createIndexObject.getIndexOptions().get();
        Assert.assertEquals("portuguese", indexOptions.getDefaultLanguage());
        Assert.assertTrue(indexOptions.isBackground());

        Bson partialFilterExpressionExpected = Document.parse("{name: {$exists: true}}");
        Assert.assertEquals(partialFilterExpressionExpected, indexOptions.getPartialFilterExpression());

        Collation collationExpected = Collation.builder()
                                    .locale("simple")
                                    .caseLevel(true)
                                    .collationStrength(CollationStrength.PRIMARY)
                                    .build();
        Assert.assertEquals(collationExpected, indexOptions.getCollation());
    }
}
