import java.io.File;
import org.fhcrc.honeycomb.nfpath.NFObject;

public class TestNFObject {
  public static void main(String args[]) {
    NFObject obj = new NFObject(new File(args[0]));
    System.out.println(obj.toString());
  }
}
