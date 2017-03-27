package test.ru;

import com.beust.jcommander.ParameterException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.Main;
import ru.spbau.Init;
import ru.spbau.Vcs;

import java.io.IOException;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Vcs.class)
public class CommandAndMainTest {

    @Before
    public void prepareVcs() throws Exception {
        PowerMockito.mockStatic(Vcs.class);
        PowerMockito.doNothing().when(Vcs.class);
    }

    @Test(expected = ParameterException.class)
    public void commandCheckoutBranchNullRevisionNull() throws IOException, Vcs.VcsException {
        String args[] = new String[]{"checkout"};
        Main.execute(args);
    }

    @Test(expected = ParameterException.class)
    public void commandCheckoutBranchNotNullRevisionNotNull() throws IOException, Vcs.VcsException {
        String args[] = new String[]{"checkout", "-b", "b", "-r", "r"};
        Main.execute(args);
    }

    @Test
    public void commandCheckoutBranchNullRevisionNotNull() throws Exception {
        String args[] = new String[]{"checkout", "-r", "r"};
        Main.execute(args);
    }

    @Test
    public void commandCheckoutBranchNotNullRevisionNull() throws Exception {
        String args[] = new String[]{"checkout", "-b", "b"};
        Main.execute(args);
    }

    @Test(expected = ParameterException.class)
    public void commandBranchNewNullDeleteNull() throws IOException, Vcs.VcsException {
        String args[] = new String[]{"branch"};
        Main.execute(args);
    }

    @Test(expected = ParameterException.class)
    public void commandBranchNewNotNullDeleteNotNull() throws IOException, Vcs.VcsException {
        String args[] = new String[]{"branch", "-n", "n", "-d", "d"};
        Main.execute(args);
    }

    @Test
    public void commandBranchNewNullDeleteNotNull() throws Exception {
        String args[] = new String[]{"branch", "-d", "d"};
        Main.execute(args);
    }

    @Test
    public void commandBranchNewNotNullDeleteNull() throws Exception {
        String args[] = new String[]{"branch", "-n", "n"};
        Main.execute(args);
    }

    @Test(expected = ParameterException.class)
    public void noArgsTest() throws IOException, Vcs.VcsException {
        Main.execute(new String[0]);
    }


}
