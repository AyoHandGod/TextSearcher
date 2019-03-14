import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FileSearchAppTest {
    FileSearchApp app;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void runWithoutArgsMessageTest(){
        FileSearchApp.main(new String[] {"./", ".*", "testZip.zip"});
    }
}
