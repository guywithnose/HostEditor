/*
 * File: MainWindow.java Author: Robert Bittle <guywithnose@gmail.com>
 */
package ui;

import hosts.HostFileCreator;
import hosts.HostFileCreator.IPInfo;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import data.FileReader;
import data.FileWriter;

/**
 * The Class MainWindow.
 */
public class MainWindow extends JFrame implements Observer
{
  /** The host name list. */
  private HostNameList hostNameList;

  /** The conf object. */
  private HostFileCreator hfc;

  /** The server list. */
  private ServerList serverList;

  /** The alias list. */
  private AliasList aliasList;

  private File openedFile;
  
  private JSONObject preferences;

  public MainWindow()
  {
    readPreferences();
    try {
        initialize(new File(preferences.getString("confFile")));
    } catch (JSONException e) {
        selectFile();
    }
    if (!isVisible()) {
      System.exit(0);
    }
  }

  public void selectFile()
  {
    while (true)
    {
      JFileChooser findConf = new JFileChooser(new File("."));
      int result = findConf.showDialog(this, "Open conf file");
      if (result == JFileChooser.APPROVE_OPTION)
      {
        try
        {
          initialize(findConf.getSelectedFile());
          writePreferences("confFile", findConf.getSelectedFile().toString());
          return;
        } catch (JSONException e)
        {
          JOptionPane.showMessageDialog(this, "Invalid conf file.");
        }
      } else {
        return;
      }
    }
  }

  private void readPreferences()
  {
      String preferenceData = FileReader.getFileContents("preferences.json");
      try {
          preferences = new JSONObject(preferenceData);
      } catch (Exception e) {
          preferences = new JSONObject();
      }
  }

  private void writePreferences(String name, String value)
  {
      try {
          preferences.put(name, value);
        FileWriter.putFileContents("preferences.json", preferences.toString(4));
    } catch (JSONException e) {
        e.printStackTrace();
    }
  }

  /**
   * Instantiates a new main window.
   * 
   * @param fileName
   *          the file name
   * @throws JSONException
   */
  public void initialize(File file) throws JSONException
  {
    String conf = FileReader.getFileContents(file);
    openedFile = file;
    JSONObject confObject;
    confObject = new JSONObject(conf);
    hfc = new HostFileCreator(confObject);
    setTitle("Host Editor");
    setSize(800, 600);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    Container content = getContentPane();
    content.removeAll();
    setMenuBar(new MainMenu(this));
    content.setBackground(Color.white);
    content.setLayout(new FlowLayout());
    hostNameList = new HostNameList(this, hfc.getAllIPInfo());
    serverList = new ServerList(this, hfc.getAllServers());
    aliasList = new AliasList(this, hfc.getAllAliases());
    content.add(hostNameList.hostListPanel);
    content.add(aliasList.aliasListPanel);
    content.add(serverList.serverListPanel);
    setVisible(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  public void update(Observable observable,
      @SuppressWarnings("unused") Object arg)
  {
    if (observable == hostNameList)
    {
      List<String> aliases = new ArrayList<String>();
      List<String> servers = new ArrayList<String>();
      for (IPInfo host : hostNameList.getSelectedHosts())
      {
        aliases.add(host.alias);
        if (host.ipName != null)
        {
          servers.add(host.ipName);
        }
      }
      aliasList.select(aliases);
      if (servers.size() == 0)
      {
        serverList.select("dns");
      } else
      {
        serverList.select(servers);
      }
    } else if (observable == aliasList)
    {
      serverList.select(hfc.getAliasIpName(aliasList.getSelectedAlias()));
      if (hostNameList.getSelectedHosts().size() == 0) {
          // TODO Select affected sites
      } else {
          for (IPInfo host : hostNameList.getSelectedHosts()) {
            hfc.setSiteToAlias(host.hostName, aliasList.getSelectedAlias());
          }
          hostNameList.update(hfc.getAllIPInfo());
      }
    } else if (observable == serverList)
    {
      aliasList.select(null);
      if (hostNameList.getSelectedHosts().size() == 0) {
          // TODO Select affected sites and aliases
      } else {
          for (IPInfo host : hostNameList.getSelectedHosts()) {
            hfc.setSiteToServer(host.hostName, serverList.getSelectedServer());
          }
          hostNameList.update(hfc.getAllIPInfo());
      }
    }
  }

  /**
   * @param actionCommand
   */
  public void sendAction(String actionCommand)
  {
      String hostDir;
      if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
          hostDir = "C:\\Windows\\System32\\drivers\\etc";
      } else {
          hostDir = "/etc";
      }
    switch (actionCommand)
    {
    case "Open":
      selectFile();
      break;
    case "Save":
      if (!hfc.save(openedFile))
      {
        JOptionPane.showMessageDialog(this, "Unable to write to file.");
      }
      break;
    case "Save As":
      JFileChooser saveAsFile = new JFileChooser(new File("."));
      int result = saveAsFile.showSaveDialog(this);
      if (result == JFileChooser.APPROVE_OPTION)
      {
        if (!hfc.save(saveAsFile.getSelectedFile()))
        {
          JOptionPane.showMessageDialog(this, "Unable to write to file.");
        } else {
            openedFile = saveAsFile.getSelectedFile();
        }
      }
      break;
    case "Export":
            try {
                String hostFile = preferences.getString("hostFile");
                if (!hfc.export(new File(hostFile)))
                {
                    sendAction("Export As");
                }
            } catch (JSONException e) {
                sendAction("Export As");
            }
        break;
    case "Export As":
        JFileChooser exportHost = new JFileChooser(new File(hostDir));
        int exportResult = exportHost.showSaveDialog(this);
        if (exportResult == JFileChooser.APPROVE_OPTION)
        {
          if (!hfc.export(exportHost.getSelectedFile()))
          {
            JOptionPane.showMessageDialog(this, "Unknown error.");
          } else {
              writePreferences("hostFile", exportHost.getSelectedFile().toString());
          }
        }
        break;
    }
  }
}
