package test.ru.spbau.zhidkov.console;

import com.beust.jcommander.ParameterException;
import org.junit.Test;
import ru.spbau.zhidkov.console.Main;
import ru.spbau.zhidkov.vcs.commands.Vcs;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ParseArgumentsExceptionTest {

    private Vcs vcs = mock(Vcs.class);

    @Test(expected = ParameterException.class)
    public void commandCheckoutBranchNullRevisionNull() throws IOException, Vcs.VcsException {
        String args[] = new String[]{"checkout"};
        Main.execute(vcs, args);
    }

    @Test(expected = ParameterException.class)
    public void commandCheckoutBranchNotNullRevisionNotNull() throws IOException, Vcs.VcsException {
        String args[] = new String[]{"checkout", "-b", "b", "-r", "r"};
        Main.execute(vcs, args);
    }

    @Test
    public void commandCheckoutBranchNullRevisionNotNull() throws Vcs.VcsException, IOException {
        String args[] = new String[]{"checkout", "-r", "r"};
        Main.execute(vcs, args);
    }

    @Test
    public void commandCheckoutBranchNotNullRevisionNull() throws Vcs.VcsException, IOException {
        String args[] = new String[]{"checkout", "-b", "b"};
        Main.execute(vcs, args);
    }

    @Test(expected = ParameterException.class)
    public void commandBranchNewNullDeleteNull() throws IOException, Vcs.VcsException {
        String args[] = new String[]{"branch"};
        Main.execute(vcs, args);
    }

    @Test(expected = ParameterException.class)
    public void commandBranchNewNotNullDeleteNotNull() throws IOException, Vcs.VcsException {
        String args[] = new String[]{"branch", "-n", "n", "-d", "d"};
        Main.execute(vcs, args);
    }

    @Test
    public void commandBranchNewNullDeleteNotNull() throws Exception {
        String args[] = new String[]{"branch", "-d", "d"};
        Main.execute(vcs, args);
    }

    @Test
    public void commandBranchNewNotNullDeleteNull() throws Exception {
        String args[] = new String[]{"branch", "-n", "n"};
        Main.execute(vcs, args);
    }

    @Test(expected = ParameterException.class)
    public void noArgsTest() throws IOException, Vcs.VcsException {
        Main.execute(vcs, new String[0]);
    }

    @Test(expected = ParameterException.class)
    public void emptyAdd() throws IOException, Vcs.VcsException {
        String[] args = new String[]{"add"};
        Main.execute(vcs, args);
    }

    @Test(expected = ParameterException.class)
    public void emptyRm() throws IOException, Vcs.VcsException {
        String[] args = new String[]{"rm"};
        Main.execute(vcs, args);
    }


}
