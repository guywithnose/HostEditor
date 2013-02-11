/*
 * File: HostNameList.java Author: Robert Bittle <guywithnose@gmail.com>
 */
package ui;

import hosts.HostFileCreator.IPInfo;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The Class HostNameList.
 */
public class HostNameList extends Observable
{

  /** The host list. */
  private JList<IPInfo> hostList;

  /** The host list panel. */
  public JPanel hostListPanel;

  JTextField hostSearch;

  CellSearcher searcher;

  /**
   * Instantiates a new host name list.
   * 
   * @param window
   *          the window
   * @param hostNames
   *          the host names
   */
  public HostNameList(MainWindow window, List<IPInfo> hostNames)
  {
    hostListPanel = new JPanel();
    searcher = new CellSearcher(hostNames);
    hostList = new JList<IPInfo>(searcher);
    addObserver(window);
    hostList.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent event)
      {
        if (event.getValueIsAdjusting())
        {
          notifyObservers();
        }
      }
    });
    hostListPanel.setLayout(new BoxLayout(hostListPanel, 1));
    hostSearch = new JTextField("Search", 20);
    hostSearch.setForeground(Color.gray);
    hostSearch.addFocusListener(new FocusListener()
    {
      @Override
      public void focusLost(FocusEvent event)
      {
        JTextField textField = (JTextField) event.getSource();
        if ("".equals(textField.getText()))
        {
          textField.setText("Search");
          textField.setForeground(Color.gray);
        }
      }

      @Override
      public void focusGained(FocusEvent event)
      {
        JTextField textField = (JTextField) event.getSource();
        if ("Search".equals(textField.getText()))
        {
          textField.setText("");
          textField.setForeground(Color.black);
        }
      }
    });
    hostSearch.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyReleased(KeyEvent event)
      {
        JTextField textField = (JTextField) event.getSource();
        searcher.filterList(textField.getText());
      }
    });
    hostListPanel.add(hostSearch);
    hostListPanel.add(new JScrollPane(hostList));
    hostList.requestFocusInWindow();
  }

  /**
   * Gets the selected alias.
   * 
   * @return the selected alias
   */
  public List<IPInfo> getSelectedHosts()
  {
    return hostList.getSelectedValuesList();
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

  private class CellSearcher implements ListModel<IPInfo>
  {
    TreeSet<IPInfo> info;
    List<IPInfo> filteredInfo;
    List<ListDataListener> listeners = new ArrayList<ListDataListener>();

    public CellSearcher(List<IPInfo> newInfo)
    {
      info = new TreeSet<IPInfo>(newInfo);
      filterList("");
    }

    /**
     * Filter list.
     * 
     * @param filter
     *          the filter
     */
    public void filterList(String filter)
    {
      filteredInfo = new ArrayList<IPInfo>(info);
      for (IPInfo host : info)
      {
        if (host.toString().indexOf(filter) < 0)
        {
          filteredInfo.remove(host);
        }
      }
      for (ListDataListener listener : listeners)
      {
        listener.contentsChanged(new ListDataEvent(this,
            ListDataEvent.CONTENTS_CHANGED, 0, filteredInfo.size()));
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener
     * )
     */
    @Override
    public void addListDataListener(ListDataListener listener)
    {
      listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getElementAt(int)
     */
    @Override
    public IPInfo getElementAt(int position)
    {
      return filteredInfo.get(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getSize()
     */
    @Override
    public int getSize()
    {
      return filteredInfo.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.
     * ListDataListener)
     */
    @Override
    public void removeListDataListener(ListDataListener listener)
    {
      listeners.remove(listener);
    }
  }

  /**
   * @param allIPInfo
   */
  public void update(List<IPInfo> hostNames)
  {
    List<String> currentSelection = new ArrayList<String>();
    for (IPInfo info : getSelectedHosts())
    {
      currentSelection.add(info.hostName);
    }
    searcher = new CellSearcher(hostNames);
    hostList.setModel(searcher);
    if (currentSelection.size() > 0)
    {
      ListModel<IPInfo> model = hostList.getModel();
      List<Integer> indicies = new ArrayList<Integer>();
      for (int i = 0; i < model.getSize(); i++)
      {
        if (currentSelection.contains(model.getElementAt(i).hostName))
        {
          indicies.add(i);
        }
      }
      int[] intIndicies = new int[indicies.size()];
      for (int i = 0; i < intIndicies.length; i++)
      {
        intIndicies[i] = indicies.get(i);
      }
      hostList.setSelectedIndices(intIndicies);
    }
  }
}
