package test.ru.spbau;

import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public abstract class ParametriziedThreeCommitsTest {

    public ParametriziedThreeCommitsTest(Map<Path, String> fcommit1Add, List<Path> fcommit1Rm,
                                         Map<Path, String> fcommit2Add, List<Path> fcommit2Rm,
                                         Map<Path, String> fcommit3Add, List<Path> fcommit3Rm) {
        this.fcommit1Add = fcommit1Add;
        this.fcommit1Rm = fcommit1Rm;
        this.fcommit2Add = fcommit2Add;
        this.fcommit2Rm = fcommit2Rm;
        this.fcommit3Add = fcommit3Add;
        this.fcommit3Rm = fcommit3Rm;
    }

    private Map<Path, String> fcommit1Add;
    private List<Path> fcommit1Rm;

    private Map<Path, String> fcommit2Add;
    private List<Path> fcommit2Rm;

    private Map<Path, String> fcommit3Add;
    private List<Path> fcommit3Rm;

    protected VcsCommit commit1 = mock(VcsCommit.class);
    protected VcsCommit commit2 = mock(VcsCommit.class);
    protected VcsCommit commit3 = mock(VcsCommit.class);

    protected void initCommits() {
        when(commit1.getChildrenAdd()).thenReturn(fcommit1Add);
        when(commit1.getChildrenRm()).thenReturn(fcommit1Rm);
        when(commit1.getPrevCommitHash()).thenReturn("commit2.hash");

        when(commit2.getChildrenAdd()).thenReturn(fcommit2Add);
        when(commit2.getChildrenRm()).thenReturn(fcommit2Rm);
        when(commit2.getPrevCommitHash()).thenReturn("commit3.hash");

        when(commit3.getChildrenAdd()).thenReturn(fcommit3Add);
        when(commit3.getChildrenRm()).thenReturn(fcommit3Rm);
        when(commit3.getPrevCommitHash()).thenReturn(CommitHandler.getInitialCommitPrevHash());
    }

}
