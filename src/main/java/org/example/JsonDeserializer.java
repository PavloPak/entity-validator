package org.example;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NumericNode;
import org.javatuples.Pair;
import java.io.IOException;
import java.util.*;

import static org.example.RecordStatusConstants.NULL_VALUE;

public class JsonDeserializer extends StdDeserializer<Map<String, List<Pair<String, Float>>>> {

    protected JsonDeserializer() {
        this(null);
    }
    protected JsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Map<String, List<Pair<String, Float>>> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {

        Map<String, List<Pair<String, Float>>> result = new HashMap<>();
        JsonNode root = jp.getCodec().readTree(jp);

        root.fields().forEachRemaining(stringJsonNodeEntry -> {

            String asOfDate = stringJsonNodeEntry.getKey();
            JsonNode nodeObject = stringJsonNodeEntry.getValue();

            List<Pair<String, Float>> observations = new ArrayList<>();

            nodeObject.fields().forEachRemaining(nestedEntry -> {

                String observationDate = nestedEntry.getKey();
                JsonNode value = nestedEntry.getValue();

                float price = value instanceof NumericNode ? value.floatValue() : NULL_VALUE;

                observations.add(new Pair<>(observationDate, price));
            });
            result.put(asOfDate, observations);
        });

        return result;
    }
}
