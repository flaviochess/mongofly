package com.github.mongofly.core.commands.createindex;

import com.github.mongofly.core.commands.utils.ConvertCommand;
import com.github.mongofly.core.commands.utils.GetCollation;
import com.github.mongofly.core.exceptions.MongoflyException;
import com.google.common.base.CaseFormat;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CreateIndexConvert {

    private static final int COMMAND_MIN_PARTS = 1;

    private static final int COMMAND_MAX_PARTS = 2;

    private static final int COMMAND_KEYS_POSITION = 0;

    private static final int COMMAND_OPTIONS_PARAMETERS_POSITION = 1;

    private static final String EXPIRE_AFTER_SECONDS = "expireAfterSeconds";

    public static CreateIndexObject convert(String command) {

        List<Document> createIndexParts = ConvertCommand.toCommandParts(command, COMMAND_MIN_PARTS, COMMAND_MAX_PARTS);

        Document keys = createIndexParts.get(COMMAND_KEYS_POSITION);

        Optional<IndexOptions> indexOptions = Optional.empty();

        if (createIndexParts.size() == COMMAND_MAX_PARTS) {

            Document options = createIndexParts.get(COMMAND_OPTIONS_PARAMETERS_POSITION);

            indexOptions = convertOptions(options);
        }

        return new CreateIndexObject(keys, indexOptions);
    }

    private static Optional<IndexOptions> convertOptions(Document options) {

        IndexOptions indexOptions = new IndexOptions();

        for (Field field : indexOptions.getClass().getDeclaredFields()) {

            String fieldName = field.getName();
            String fieldNameSnackCase = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);

            if (!options.containsKey(fieldName) && !options.containsKey(fieldNameSnackCase)) {
                continue;
            }

            String commandParameterName;

            if (options.containsKey(fieldName)) {
                commandParameterName = fieldName;
            } else {
                commandParameterName = fieldNameSnackCase;
            }

            try {

                if (GetCollation.COLLATION.equals(fieldName)) {
                    indexOptions.collation(GetCollation.get(options).orElse(Collation.builder().build()));
                    continue;
                }

                if (EXPIRE_AFTER_SECONDS.equals(fieldName)) {

                    Long expireAfterSeconds = options.getInteger(EXPIRE_AFTER_SECONDS).longValue();
                    indexOptions.expireAfter(expireAfterSeconds, TimeUnit.SECONDS);
                    continue;
                }

                Method setMethod = indexOptions.getClass().getDeclaredMethod(fieldName, field.getType());
                setMethod.invoke(indexOptions, options.get(commandParameterName));

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

                log.error("Problems to work with {} parameter", commandParameterName, e);
                throw new MongoflyException(e);
            }
        }

        return Optional.of(indexOptions);
    }
}
