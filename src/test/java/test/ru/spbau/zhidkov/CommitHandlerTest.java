package test.ru.spbau.zhidkov;

import com.google.common.collect.ImmutableMap;
import com.sun.istack.internal.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsCommit;
import test.ru.spbau.ParametriziedThreeCommitsTest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Нико on 07.04.2017.
 */
@RunWith(Parameterized.class)
public class CommitHandlerTest extends ParametriziedThreeCommitsTest {

    private List<Path> ans;
    public CommitHandlerTest(Map<Path, String> fcommit1Add, List<Path> fcommit1Rm, Map<Path, String> fcommit2Add, List<Path> fcommit2Rm, Map<Path, String> fcommit3Add, List<Path> fcommit3Rm, List<Path> ans) {
        super(fcommit1Add, fcommit1Rm, fcommit2Add, fcommit2Rm, fcommit3Add, fcommit3Rm);
        this.ans = ans;
    }

    @Test
    public void getAllActiveFilesOnlyAddedTest() throws IOException {
        initCommits();

        VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
        when(vcsFileHandler.getCommit(anyString())).thenReturn(commit1, commit2, commit3);

        List<Path> filesInRev = new CommitHandler(vcsFileHandler).getAllActiveFilesInRevision("hash");

        assertTrue(CollectionUtils.isEqualCollection(filesInRev, ans));
    }

    @NotNull
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b"),
                        Collections.emptyList(),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        ImmutableMap.of(Paths.get("d"), "hash.d", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        Arrays.asList(Paths.get("a"), Paths.get("b"), Paths.get("c"), Paths.get("d"))
                },
                {
                        Collections.emptyMap(),
                        Arrays.asList(Paths.get("c"), Paths.get("a")),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("c"), "hash.c"),
                        Collections.singletonList(Paths.get("d")),

                        ImmutableMap.of(Paths.get("d"), "hash.d", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        Collections.emptyList()
                },
                {
                        Collections.emptyMap(),
                        Collections.singletonList(Paths.get("b")),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b"),
                        Collections.singletonList(Paths.get("c")),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        Collections.singletonList(Paths.get("a"))
                }
        });
    }

}
