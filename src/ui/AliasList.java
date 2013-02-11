/*
 * File: AliasList.java Author: Robert Bittle <guywithnose@gmail.com>
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
 * The Class AliasList.
 */
public class AliasList extends Observable
{
  /** The alias list panel. */
  public JPanel aliasListPanel;

  /** The alias list. */
  private JList<String> aliasList;

  boolean updating;

  /**
   * Instantiates a new alias list.
   * 
   * @param window
   *          the window
   * @param newAliases
   *          the new aliases
   */
  public AliasList(MainWindow window, List<String> aliases)
  {
    aliasListPanel = new JPanel();
    aliasList = new JList<String>(
        new TreeSet<String>(aliases).toArray(new String[] {}));
    aliasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    aliasList.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(
          @SuppressWarnings("unused") ListSelectionEvent arg0)
      {
        if (!updating) {
          notifyObservers();
        }
      }
    });
    addObserver(window);
    aliasListPanel.setLayout(new FlowLayout());
    aliasListPanel.add(aliasList);
  }

  /**
   * Gets the selected alias.
   * 
   * @return the selected alias
   */
  public String getSelectedAlias()
  {
    return aliasList.getSelectedValue();
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
   * Select.
   * 
   * @param alias
   *          the alias
   */
  public void select(List<String> aliasNames)
  {
    if (aliasNames == null)
    {
      updating = true;
      aliasList.clearSelection();
      updating = false;
      return;
    }
    if (aliasNames.size() > 0)
    {
      ListModel<String> model = aliasList.getModel();
      List<Integer> indicies = new ArrayList<Integer>();
      for (int i = 0; i < model.getSize(); i++)
      {
        if (aliasNames.contains(model.getElementAt(i)))
        {
          indicies.add(i);
        }
      }
      int[] intIndicies = new int[indicies.size()];
      for (int i = 0; i < intIndicies.length; i++)
      {
        intIndicies[i] = indicies.get(i);
      }
      aliasList
          .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      updating = true;
      aliasList.setSelectedIndices(intIndicies);
      updating = false;
      aliasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    } else
    {
      updating = true;
      aliasList.clearSelection();
      updating = false;
    }
  }
}
