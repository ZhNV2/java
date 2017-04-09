package ru.spbau.zhidkov.vcs.file;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Нико on 08.04.2017.
 */
public class ObjectSerializer {
    public String serialize(Object o) throws IOException {

        return new GsonBuilder()
                .registerTypeAdapter(Path.class, new PathSerializer())
                .registerTypeAdapter(new TypeToken<List<Path>>() {}.getType(), new ListSerializer())
                .create()
                .toJson(o);
    }

    private static class PathSerializer implements JsonSerializer<Path> {
        @Override
        public JsonElement serialize(Path src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
    private static class ListSerializer implements JsonSerializer<List<Path>> {
        @Override
        public JsonElement serialize(List<Path> src, Type typeOfSrc, JsonSerializationContext context) {
            String s = src.stream().map(Path::toString).collect(Collectors.joining(","));
            return new JsonPrimitive(s);
        }
    }
}
