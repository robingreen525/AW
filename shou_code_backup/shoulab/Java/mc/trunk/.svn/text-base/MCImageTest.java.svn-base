import org.fhcrc.honeycomb.mc.MCImage;
import java.io.File;
import java.io.FileNotFoundException;
import static org.junit.Assert.*;
import org.junit.*;

public class MCImageTest {
  private MCImage mcimage;
  private File image_path = 
    new File("test-files/my_experiment/A01a/WL0/" +
             "my_experiment_0000_A01a_WL0.tif");
  private String image_name;

  @Test
  public void testConstructor() throws Exception, FileNotFoundException {
    System.out.println("Testing construction...");
    try {
      mcimage = new MCImage(image_path);
      System.out.println(mcimage);
    } catch (FileNotFoundException fne) {
      fne.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    MCImageTest tester = new MCImageTest();

    try {
      tester.testConstructor();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
