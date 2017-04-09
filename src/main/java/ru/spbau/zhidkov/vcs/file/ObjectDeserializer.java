package ru.spbau.zhidkov.vcs.file;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Нико on 08.04.2017.
 */
public class ObjectDeserializer {

    public Object deserialize(String content, Class<?> clazz) throws IOException {
        return new GsonBuilder()
                .registerTypeAdapter(Path.class, new PathDeserializer())
                .registerTypeAdapter(new TypeToken<List<Path>>() {}.getType(), new ListDeserializer())
                .create()
                .fromJson(content, clazz);
    }

    private static class PathDeserializer implements JsonDeserializer<Path> {
        @Override
        public Path deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return Paths.get(json.getAsJsonPrimitive().getAsString());
        }
    }

    private static class ListDeserializer implements JsonDeserializer<List<Path>> {
        @Override
        public List<Path> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String jsonString = json.getAsJsonPrimitive().getAsString();
            if (jsonString.equals("")) return new ArrayList<>();
            return Arrays.stream(jsonString
                    .split(","))
                    .map(Paths::get)
                    .collect(Collectors.toList());
        }
    }
}
