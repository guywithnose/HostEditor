/*
 * File:         HostFileCreator.java
 * Author:       Robert Bittle <guywithnose@gmail.com>
 */
package hosts;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class HostFileCreator
{
  private JSONObject sites;
  private JSONObject aliases;
  private JSONObject ips;
  private ArrayList<String> errors = new ArrayList<String>();

  public HostFileCreator(JSONObject conf) throws JSONException
  {
    sites = conf.getJSONObject("sites");
    aliases = conf.getJSONObject("aliases");
    ips = conf.getJSONObject("IPs");
  }

  public String buildHostFile()
  {
    try
    {
      StringBuilder hostbuilder = new StringBuilder();
      @SuppressWarnings("unchecked")
      Iterator<String> siteIterator = sites.keys();
      while (siteIterator.hasNext())
      {
        String siteName = siteIterator.next();
        String ipName = sites.getString(siteName);
        String IP = getIP(ipName);
        if (IP != null)
        {
          hostbuilder.append(IP);
          hostbuilder.append(" ");
          hostbuilder.append(siteName);
          hostbuilder.append("\n");
        }
      }
      return hostbuilder.toString();
    } catch (JSONException e)
    {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * @param siteName
   * @return
   * @throws JSONException
   */
  private String getIP(String ipName) throws JSONException
  {
    if ("dns".equalsIgnoreCase(ipName)) {
      return null;
    }
    if (ips.has(ipName))
    {
      return ips.getString(ipName);
    } else if (aliases.has(ipName))
    {
      String aliasIpName = aliases.getString(ipName);
      if ("dns".equalsIgnoreCase(aliasIpName)) {
        return null;
      }
      if (ips.has(aliasIpName)) {
        return ips.getString(aliasIpName);
      }
      errors.add("Invalid ip name: " + aliasIpName);
      System.err.println("Invalid ip name: " + aliasIpName);
      return null;
    }
    errors.add("Invalid ip name: " + ipName);
    System.err.println("Invalid ip name: " + ipName);
    return null;
  }

  /**
   * Get Errors.
   *
   * @return the errors
   */
  public ArrayList<String> getErrors()
  {
    return errors;
  }
}