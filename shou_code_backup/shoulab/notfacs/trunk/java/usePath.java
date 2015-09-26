import java.io.File;

public class usePath {
  public static void main(String args[]) {
    NFPath nfpath = new NFPath(new File(args[0]));
    nfpath.setTimepoint(0);
    nfpath.setPosition(0);
    File[] zero = nfpath.getFiles();
    for (File z : zero) {
      System.out.println(z);
    }
  }
}

