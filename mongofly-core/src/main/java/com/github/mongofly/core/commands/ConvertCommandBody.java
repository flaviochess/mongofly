package com.github.mongofly.core.commands;

import com.github.mongofly.core.utils.MongoflyException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.github.mongofly.core.converts.CommandConvert.CURLY_BRACES_CLOSE;
import static com.github.mongofly.core.converts.CommandConvert.CURLY_BRACES_OPEN;

public class ConvertCommandBody {

    public static List<Document> toDocumentList(String commandBody) {

        List<Document> documents = new ArrayList();

        Stack<Character> bracesStack = new Stack();
        int firstBraceOfJson = -1;

        for (int positionOfLetter = 0; positionOfLetter < commandBody.length(); positionOfLetter++) {

            if (commandBody.charAt(positionOfLetter) == CURLY_BRACES_OPEN) {

                bracesStack.push(commandBody.charAt(positionOfLetter));

                if (firstBraceOfJson == -1) {
                    firstBraceOfJson = positionOfLetter;
                }

                continue;
            }

            if (commandBody.charAt(positionOfLetter) == CURLY_BRACES_CLOSE) {

                bracesStack.pop();

                if (bracesStack.isEmpty()) {

                    String commandBodyJson = commandBody.substring(firstBraceOfJson, positionOfLetter + 1);
                    documents.add(convertToDocument(commandBodyJson));
                    firstBraceOfJson = -1;
                }
            }

        }

        if (!bracesStack.isEmpty()) {
            throw new MongoflyException("Bad bson exception. There are \"{\" without closing: " + commandBody);
        }

        return documents;
    }

    private static Document convertToDocument(String commandBody) {

        return Document.parse(commandBody);
    }
}
