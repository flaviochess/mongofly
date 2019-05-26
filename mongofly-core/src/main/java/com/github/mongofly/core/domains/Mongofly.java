package com.github.mongofly.core.domains;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.Date;

@Getter
@Setter
public class Mongofly {

    private String id;

    private String version;

    private String script;

    private Date executedOn;

    private boolean success;

    public Document toDocument() {

        Document document = new Document();

        document.put("version", version);
        document.put("script", script);
        document.put("executedOn", executedOn);
        document.put("success", success);

        return document;
    }

    public static Mongofly fromDocument(Document document) {

        if (document == null) {
            return null;
        }

        Mongofly mongofly = new Mongofly();

        if (document.containsKey("id")) {
            mongofly.setId(document.getString("id"));
        }

        if (document.containsKey("version")) {
            mongofly.setVersion(document.getString("version"));
        }

        if (document.containsKey("script")) {
            mongofly.setScript(document.getString("script"));
        }

        if (document.containsKey("executedOn")) {
            mongofly.setExecutedOn(document.getDate("executedOn"));
        }

        if (document.containsKey("success")) {
            mongofly.setSuccess(document.getBoolean("success"));
        }

        return mongofly;
    }

}
