package com.github.mongofly.core.utils;

import com.mongodb.WriteConcern;
import org.bson.Document;

import java.util.Optional;
import java.util.OptionalInt;

import static com.github.mongofly.core.converts.CommandConvert.WRITE_CONCERN;

public class GetWriteConcern {

    public static Optional<WriteConcern> get(Document writeConcern) {

        if(!isWriteConcern(writeConcern)) {
            return Optional.empty();
        }

        return Optional.of(convertWriteConcern(writeConcern));
    }

    private static boolean isWriteConcern(Document document) {
        return document.containsKey(WRITE_CONCERN);
    }

    private static WriteConcern convertWriteConcern(Document writeConcern) {

        OptionalInt w = OptionalInt.empty();
        OptionalInt wTimeoutMS = OptionalInt.empty();

        if(writeConcern.containsKey("w")) {
            w = OptionalInt.of(writeConcern.getInteger("w"));

            if(writeConcern.containsKey("wtimeout")) {
                wTimeoutMS = OptionalInt.of(writeConcern.getInteger("wtimeout"));
            }
        }

        if(w.isPresent()) {

            if(wTimeoutMS.isPresent()) {
                return new WriteConcern(w.getAsInt(), wTimeoutMS.getAsInt());
            } else {
                return new WriteConcern(w.getAsInt());
            }
        }

        throw new MongoflyException("");
    }
}
