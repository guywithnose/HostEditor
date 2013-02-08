/*
 * File: ServerList.java Author: Robert Bittle <guywithnose@gmail.com>
 */
package ui;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.TreeSet;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The Class ServerList.
 */
public class ServerList extends Observable
{
  /** The server list panel. */
  public JPanel serverListPanel;

  /** The server list. */
  private JList<String> serverList;

  /** The updating. */
  boolean updating;

  /**
   * Instantiates a new server list.
   * 
   * @param window
   *          the window
   * @param newServers
   *          the new servers
   */
  public ServerList(MainWindow window, List<String> servers)
  {
    serverListPanel = new JPanel();
    serverList = new JList<String>(
        new TreeSet<String>(servers).toArray(new String[] {}));
    serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    serverList.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(
          @SuppressWarnings("unused") ListSelectionEvent arg0)
      {
        if (!updating)
        {
          notifyObservers();
        }
      }
    });
    addObserver(window);
    serverListPanel.setLayout(new FlowLayout());
    serverListPanel.add(serverList);
  }

  /**
   * Gets the selected server.
   * 
   * @return the selected server
   */
  public String getSelectedServer()
  {
    return serverList.getSelectedValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Observable#notifyObservers()
   */
  @Override
  public void notifyObservers()
  {
    setChanged();
    super.notifyObservers();
  }

  /**
   * @param aliasIpName
   */
  public void select(String serverName)
  {
    List<String> selectList = new ArrayList<String>();
    selectList.add(serverName);
    select(selectList);
  }

  /**
   * Select.
   * 
   * @param ipName
   *          the ip name
   */
  public void select(List<String> ipNames)
  {
    if (ipNames == null)
    {
      updating = true;
      serverList.clearSelection();
      updating = false;
      return;
    }
    if (ipNames.size() > 0)
    {
      ListModel<String> model = serverList.getModel();
      List<Integer> indicies = new ArrayList<Integer>();
      for (int i = 0; i < model.getSize(); i++)
      {
        if (ipNames.contains(model.getElementAt(i)))
        {
          indicies.add(i);
        }
      }
      int[] intIndicies = new int[indicies.size()];
      for (int i = 0; i < intIndicies.length; i++)
      {
        intIndicies[i] = indicies.get(i);
      }
      serverList
          .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      updating = true;
      serverList.setSelectedIndices(intIndicies);
      updating = false;
      serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    } else
    {
      updating = true;
      serverList.clearSelection();
      updating = false;
    }
  }
}
