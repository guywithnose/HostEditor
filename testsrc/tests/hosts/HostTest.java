/*
 * File:         HostTest.java
 * Author:       Robert Bittle <guywithnose@gmail.com>
 */
package tests.hosts;

import static org.junit.Assert.*;
import hosts.HostFileCreator;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import data.FileReader;

/**
 * The Class HostTest.
 */
public class HostTest
{
  
  /**
     * Test.
     * 
     * @throws JSONException
     *             the jSON exception
     */
  @SuppressWarnings("static-method")
  @Test
  public void test() throws JSONException
  {
    HostFileCreator hfc = new HostFileCreator(new JSONObject(FileReader.getFileContents("conf.json")));
    assertEquals("243.65.78.40 dev.jumpingmaniacs.com\n127.0.0.1 dev.google.com\n243.65.78.50 dev.msn.com\n127.0.0.1 dev.teddybear.com\n243.65.78.50 www.google.com\n243.65.78.70 www.bouncyballs.com\n243.65.78.60 www.yahoo.com\n243.65.78.80 www.wrappingducks.com\n", hfc.buildHostFile());
    assertEquals(1, hfc.getErrors().size());
    assertEquals("Invalid ip name: lyve", hfc.getErrors().get(0));
  }
}