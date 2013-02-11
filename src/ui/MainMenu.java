/*
 * File: MainMenu.java Author: Robert Bittle <guywithnose@gmail.com>
 */
package ui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Class MainMenu.
 */
public class MainMenu extends MenuBar
{

  /** The window. */
  MainWindow window;

  /**
   * Instantiates a new main menu.
   * 
   * @param mainWindow
   *          the main window
   */
  public MainMenu(MainWindow mainWindow)
  {
    window = mainWindow;
    add(new FileMenu());
  }

  /**
   * The Class FileMenu.
   */
  private class FileMenu extends Menu
  {
    /**
     * Instantiates a new file menu.
     */
    public FileMenu()
    {
      setLabel("File");
      String[] menuOptions = new String[]
      {
          "Open", "Save", "Save As", "Export", "Export As"
      };
      for (String option : menuOptions)
      {
        MenuItem item = new MenuItem(option);
        item.addActionListener(new MenuAction());
        add(item);
      }
    }
  }

  /**
   * The Class MenuAction.
   */
  private class MenuAction implements ActionListener
  {
    /**
     * Instantiates a new menu action.
     */
    public MenuAction()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
      window.sendAction(e.getActionCommand());
    }
  }
}
