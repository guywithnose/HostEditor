package data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The Class FileReader.
 */
public class FileWriter
{
  /**
   * Gets the file contents.
   * 
   * @param file
   *          the file name
   * @return the file contents
   */
  public static boolean putFileContents(File file, String data)
  {
    try
    {
      FileOutputStream fos;
      fos = new FileOutputStream(file);
      fos.write(data.getBytes());
      fos.close();
      return true;
    } catch (IOException e)
    {
      return false;
    }
  }

  /**
   * @param string
   * @return
   */
  public static boolean putFileContents(String fileName, String data)
  {
    return putFileContents(new File(fileName), data);
  }
}