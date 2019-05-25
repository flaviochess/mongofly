package com.github.mongofly.core.commands.utils;

import com.mongodb.client.model.*;

import org.bson.Document;

import java.util.Optional;

public class GetCollation {

    public static final String COLLATION = "collation";
    public static final String LOCALE = "locale";
    public static final String CASE_LEVEL = "caseLevel";
    public static final String CASE_FIRST = "caseFirst";
    public static final String STRENGTH = "strength";
    public static final String NUMERIC_ORDERING = "numericOrdering";
    public static final String ALTERNATE = "alternate";
    public static final String MAX_VARIABLE = "maxVariable";
    public static final String NORMALIZATION = "normalization";
    public static final String BACKWARDS = "backwards";

    public static Optional<Collation> get(Document options) {

        if(!isCollation(options)) {

            return Optional.empty();
        }

        Document collation = options.get(COLLATION, Document.class);

        return Optional.of(convertCollation(collation));
    }

    private static boolean isCollation(Document document) {
        return document.containsKey(COLLATION);
    }

    private static Collation convertCollation(Document collation) {

        Collation.Builder builder = Collation.builder();

        if(collation.containsKey(LOCALE)) {
            builder.locale(collation.getString(LOCALE));
        }

        if(collation.containsKey(CASE_LEVEL)) {
            builder.caseLevel(collation.getBoolean(CASE_LEVEL));
        }

        if(collation.containsKey(CASE_FIRST)) {
            builder.collationCaseFirst(CollationCaseFirst.fromString(collation.getString(CASE_FIRST)));
        }

        if(collation.containsKey(STRENGTH)) {
            builder.collationStrength(CollationStrength.fromInt(collation.getInteger(STRENGTH)));
        }

        if(collation.containsKey(NUMERIC_ORDERING)) {
            builder.numericOrdering(collation.getBoolean(NUMERIC_ORDERING));
        }

        if(collation.containsKey(ALTERNATE)) {
            builder.collationAlternate(CollationAlternate.fromString(collation.getString(ALTERNATE)));
        }

        if(collation.containsKey(MAX_VARIABLE)) {
            builder.collationMaxVariable(CollationMaxVariable.fromString(collation.getString(MAX_VARIABLE)));
        }

        if(collation.containsKey(NORMALIZATION)) {
            builder.normalization(collation.getBoolean(NORMALIZATION));
        }

        if(collation.containsKey(BACKWARDS)) {
            builder.backwards(collation.getBoolean(BACKWARDS));
        }

        return builder.build();
    }
}
