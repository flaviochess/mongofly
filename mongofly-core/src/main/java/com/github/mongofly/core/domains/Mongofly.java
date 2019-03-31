package com.github.mongofly.core.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Mongofly {

    private String id;

    private String version;

    private String script;

    private Date executedOn;

    private boolean success;

}
