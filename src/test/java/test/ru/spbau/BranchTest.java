package test.ru.spbau;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.spbau.zhidkov.vcs.FileSystem;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.VcsObject;

import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;
import static test.TestsStaticData.basicSetup;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileSystem.class, VcsObject.class})
public class BranchTest {

    @Before
    public void mergeSetup() throws IOException {
        basicSetup();
    }

    @Test(expected = Vcs.VcsBranchActionForbiddenException.class)
    public void branchExists() throws Vcs.VcsBranchActionForbiddenException, Vcs.VcsIllegalStateException, IOException {
        when(FileSystem.exists(anyString())).thenReturn(true);
        Vcs.createBranch("branch");
    }

    @Test(expected = Vcs.VcsIllegalStateException.class)
    public void uncommittedChanges() throws Vcs.VcsConflictException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsBranchNotFoundException, Vcs.VcsIllegalStateException, IOException {
        when(FileSystem.exists(anyString())).thenReturn(false);
        when(FileSystem.getFirstLine(anyString())).thenReturn("a.txt");
        Vcs.createBranch("branch");
    }

    @Test(expected = Vcs.VcsBranchActionForbiddenException.class)
    public void mergeSameBranch() throws IOException, Vcs.VcsIllegalStateException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsBranchNotFoundException, Vcs.VcsConflictException {
        when(FileSystem.getFirstLine(anyString())).thenReturn("branch");
        Vcs.deleteBranch("branch");
    }


}
