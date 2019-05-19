package recipeNo035;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/*
  In this sample I will create jar file with dynamic library.
  This library will be extracted from a file stored inside JAR.
  Due to the fact we want to be cross system compatible we are
  creating ".lib" file instead of dylib (macOS) or so (Linux).
*/
public class Main {

  public static File createTempDirectory() throws IOException {

    final File temp;
    temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

    if (!(temp.delete())) {
      throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
    }

    if (!(temp.mkdir())) {
      throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
    }

    File subdir = new File(temp.getAbsolutePath() + File.separator + "lib");

    if (!(subdir.mkdir())) {
      throw new IOException(
          "Could not create temp directory: " + temp.getAbsolutePath() + File.separator + "lib");
    }

    return subdir;
  }

  /*
    We are extracting library file and provide location of this
    file inside temporary location
  */
  private String extractLibrary(File location, String name, String ext) {
    try {

      File tmpFile =
          new File(location.getAbsoluteFile().toPath() + File.separator + name + "." + ext);

      try {
        tmpFile.createNewFile();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      // In case it worked, we can extract lib
      if (tmpFile.exists()) {
        // First of all, let's show where do we plan to extract it
        System.out.println("Temporary file: " + tmpFile.getAbsoluteFile().toPath());

        // Now, we can get the lib file from JAR and put it inside temporary location
        InputStream link = (getClass().getResourceAsStream("/lib/" + name + "." + ext));

        // We want to overwrite existing file. This is why we are using
        //
        // java.nio.file.StandardCopyOption.REPLACE_EXISTING
        Files.copy(
            link,
            tmpFile.getAbsoluteFile().toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        return tmpFile.getAbsoluteFile().toPath().toString();
      }
      // In case something goes wrong, we will simply return null
      return null;

    } catch (IOException e) {
      // The same goes for exception - we are passing null back
      e.printStackTrace();
      return null;
    }
  }

  /*
    This is main method that
    - extracts library from JAR
    - calls native method declared inside HelloWorld class
  */
  public static void main(String[] arg) {

    File tmp = null;
    // Create temporaryDirectory
    try {
      tmp = Main.createTempDirectory();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    // Now, we need to extract library files
    Main main = new Main();
    String libSharedLocation = main.extractLibrary(tmp, "libSharedLib", "so");
    String libLocation = main.extractLibrary(tmp, "libHelloWorld", "lib");
    String libChdirLocation = main.extractLibrary(tmp, "libChdir", "so");

    // Make sure to change dir where lib dir is
    if (libLocation != null) {
      Chdir chdir =
          new Chdir(libChdirLocation, tmp.getAbsoluteFile().toPath().toString().replace("lib", ""));
      chdir.callNativeMethod();
    }

    // Once it is extracted, we can call native code
    // We are passing library location as argument
    // for HelloWorld constructor
    if (libLocation != null) {
      HelloWorld helloWorld = new HelloWorld(libLocation);
      helloWorld.callNativeMethod();
    }
  }
}
