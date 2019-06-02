package com.github.mongofly.core.commands.strictmode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConvertNumberIntTest {

    ConvertNumberInt convertNumberInt;

    @Before
    public void initClass() {

        convertNumberInt = new ConvertNumberInt();
    }

    @Test
    public void shouldSuccess_whenGive_formattedCommand_replaceNumberIntFunction_toStrictMode() {

        String command = "db.character.insert(\n" +
                "{\n" +
                    "\t\"name\" : \"Amy Pound\",\n" +
                    "\t\"season\" : NumberInt(5),\n" +
                    "\t\"codename\" : \"The girl who waited\"\n" +
                "});";

        String expectedCommand = "db.character.insert(\n" +
                "{\n" +
                "\t\"name\" : \"Amy Pound\",\n" +
                "\t\"season\" : {\"$numberInt\": \"5\"},\n" +
                "\t\"codename\" : \"The girl who waited\"\n" +
                "});";

        String strictModeCommand = convertNumberInt.toStrictMode(command);

        Assert.assertEquals("convert NumberInt to strict mode", expectedCommand, strictModeCommand);
    }

    @Test
    public void shouldSuccess_whenGive_formattedCommandWithManyNumberInt_replaceAllNumberIntFunction_toStrictMode() {

        String command = "db.newseasons.insert(\n" +
                "{\n" +
                "    \"season\": NumberInt(1),\n" +
                "    \"data\" : {\n" +
                "        \"year\": NumberInt(2015),\n" +
                "        \"Incarnation\": \"ninth doctor\"\n" +
                "    }\n" +
                "});";

        String expectedCommand = "db.newseasons.insert(\n" +
                "{\n" +
                "    \"season\": {\"$numberInt\": \"1\"},\n" +
                "    \"data\" : {\n" +
                "        \"year\": {\"$numberInt\": \"2015\"},\n" +
                "        \"Incarnation\": \"ninth doctor\"\n" +
                "    }\n" +
                "});";

        String strictModeCommand = convertNumberInt.toStrictMode(command);

        Assert.assertEquals("convert NumberInt functions to strict mode", expectedCommand, strictModeCommand);
    }

    @Test
    public void shouldSuccess_whenGive_commandWithManyNumberInt_replaceAllNumberIntFunction_toStrictMode() {

        String command = "db.newseasons.insert({\"season\": NumberInt(2),\"data\" : {\"year\": NumberInt(2016),\"Incarnation\": \"tenth doctor\"}});";

        String expectedCommand = "db.newseasons.insert({\"season\": {\"$numberInt\": \"2\"},\"data\" : {\"year\": {\"$numberInt\": \"2016\"},\"Incarnation\": \"tenth doctor\"}});";

        String strictModeCommand = convertNumberInt.toStrictMode(command);

        Assert.assertEquals("convert NumberInt functions to strict mode", expectedCommand, strictModeCommand);
    }
}
