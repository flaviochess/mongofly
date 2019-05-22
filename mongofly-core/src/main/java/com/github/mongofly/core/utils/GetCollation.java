package com.github.mongofly.core.utils;

import com.mongodb.client.model.*;

import org.bson.Document;

import java.util.Optional;

public class GetCollation {

    public static Optional<Collation> get(Document options) {

        if(!isCollation(options)) {

            return Optional.empty();
        }

        Document collation = options.get("collation", Document.class);

        return Optional.of(convertCollation(collation));
    }

    private static boolean isCollation(Document document) {
        return document.containsKey("collation");
    }

    private static Collation convertCollation(Document collation) {

        Collation.Builder builder = Collation.builder();

        if(collation.containsKey("locale")) {
            builder.locale(collation.getString("locale"));
        }

        if(collation.containsKey("caseLevel")) {
            builder.caseLevel(collation.getBoolean("caseLevel"));
        }

        if(collation.containsKey("caseFirst")) {
            builder.collationCaseFirst(CollationCaseFirst.fromString(collation.getString("caseFirst")));
        }

        if(collation.containsKey("strength")) {
            builder.collationStrength(CollationStrength.fromInt(collation.getInteger("strength")));
        }

        if(collation.containsKey("numericOrdering")) {
            builder.numericOrdering(collation.getBoolean("numericOrdering"));
        }

        if(collation.containsKey("alternate")) {
            builder.collationAlternate(CollationAlternate.fromString(collation.getString("alternate")));
        }

        if(collation.containsKey("maxVariable")) {
            builder.collationMaxVariable(CollationMaxVariable.fromString(collation.getString("maxVariable")));
        }

        if(collation.containsKey("normalization")) {
            builder.normalization(collation.getBoolean("normalization"));
        }

        if(collation.containsKey("backwards")) {
            builder.backwards(collation.getBoolean("backwards"));
        }

        return builder.build();
    }
}
