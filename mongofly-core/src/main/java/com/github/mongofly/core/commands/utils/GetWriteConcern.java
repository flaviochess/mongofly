package com.github.mongofly.core.commands.utils;

import com.github.mongofly.core.exceptions.MongoflyException;
import com.mongodb.WriteConcern;
import org.bson.Document;

import java.util.Optional;
import java.util.OptionalInt;

public class GetWriteConcern {

    public static final String WRITE_CONCERN = "writeConcern";
    public static final String W_PARAM = "w";
    public static final String W_TIMEOUT = "wtimeout";

    public static Optional<WriteConcern> get(Document options) {

        if(!isWriteConcern(options)) {
            return Optional.empty();
        }

        Document writeConcern = options.get("writeConcern", Document.class);

        return Optional.of(convertWriteConcern(writeConcern));
    }

    private static boolean isWriteConcern(Document document) {
        return document.containsKey(WRITE_CONCERN);
    }

    private static WriteConcern convertWriteConcern(Document writeConcern) {

        OptionalInt w = OptionalInt.empty();
        OptionalInt wTimeoutMS = OptionalInt.empty();

        if(writeConcern.containsKey(W_PARAM)) {
            w = OptionalInt.of(writeConcern.getInteger(W_PARAM));

            if(writeConcern.containsKey(W_TIMEOUT)) {
                wTimeoutMS = OptionalInt.of(writeConcern.getInteger(W_TIMEOUT));
            }
        }

        if(w.isPresent()) {

            if(wTimeoutMS.isPresent()) {
                return new WriteConcern(w.getAsInt(), wTimeoutMS.getAsInt());
            } else {
                return new WriteConcern(w.getAsInt());
            }
        }

        throw new MongoflyException("Invalid writeConcern, w parameter not found");
    }
}
