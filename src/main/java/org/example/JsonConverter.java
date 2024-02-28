package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.javatuples.Pair;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonConverter {

    static Map<String, List<Pair<String, Float>>> convertJson(FileReader fileReader) {

        Map<String, List<Pair<String, Float>>> jsonMap = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new JsonDeserializer());
        mapper.registerModule(module);

        try {
            jsonMap = mapper.readValue(fileReader, new TypeReference<Map<String, List<Pair<String, Float>>>>() {});
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        return jsonMap;
    }
}
