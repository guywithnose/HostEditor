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

  public MainWindow()
  {
    selectFile();
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
      for (IPInfo host : hostNameList.getSelectedHosts()) {
        hfc.setSiteToAlias(host.hostName, aliasList.getSelectedAlias());
      }
      hostNameList.update(hfc.getAllIPInfo());
    } else if (observable == serverList)
    {
      aliasList.select(null);
      for (IPInfo host : hostNameList.getSelectedHosts()) {
        hfc.setSiteToServer(host.hostName, serverList.getSelectedServer());
      }
      hostNameList.update(hfc.getAllIPInfo());
    }
  }

  /**
   * @param actionCommand
   */
  public void sendAction(String actionCommand)
  {
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
        }
      }
      break;
    case "Export":
      JFileChooser exportHost = new JFileChooser(new File("C:\\Windows\\System32\\drivers\\etc"));
      int exportResult = exportHost.showSaveDialog(this);
      if (exportResult == JFileChooser.APPROVE_OPTION)
      {
        if (!hfc.export(exportHost.getSelectedFile()))
        {
          JOptionPane.showMessageDialog(this, "Unknown error.");
        }
      }
      break;
    }
  }
}