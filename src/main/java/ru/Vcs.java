package ru;

import java.io.IOException;
import java.util.List;

/**
 * Created by Nikolay on 08.03.17.
 */
public interface Vcs {
    void init(String authorName) throws IOException;
    void commit(String message) throws IOException;
    void add(List<String> files) throws IOException;
}
