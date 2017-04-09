package ru.spbau.zhidkov.vcs.file;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**Class for serializing objects*/
public class ObjectSerializer {

    /**
     * Serializes provided object to json
     *
     * @param o object to serialize
     * @return <tt>String</tt> json representation
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public String serialize(Object o) throws IOException {
        return new GsonBuilder()
                .registerTypeAdapter(Path.class, new PathSerializer())
                .registerTypeAdapter(new TypeToken<List<Path>>() {
                }.getType(), new ListSerializer())
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
