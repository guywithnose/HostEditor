/*
 * File:         HostEditor.java
 * Author:       Robert Bittle <guywithnose@gmail.com>
 */
package hosts;

import org.json.JSONException;
import org.json.JSONObject;

import data.FileReader;

/**
 * The Class HostEditor.
 */
public class Main
{
  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args)
  {
    if (args.length == 0) {
      System.out.println("Usage: java HostEditor.java {conf}");
      return;
    }
    String conf = FileReader.getFileContents(args[0]);
    if (conf == null) {
      System.out.println("Conf file not found.");
      return;
    }
    try {
      JSONObject confObject = new JSONObject(conf);
      HostFileCreator hfc = new HostFileCreator(confObject);
      System.out.println(hfc.buildHostFile());
      for (String error : hfc.getErrors()) {
          System.err.println(error);
      }
    } catch (JSONException e) {
      System.out.println("Conf file not valid JSON.");
      System.out.println(e.getMessage());
      return;
    }
  }
}
