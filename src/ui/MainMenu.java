/*
 * File: MainMenu.java Author: Robert Bittle <guywithnose@gmail.com>
 */
package ui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

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
      MenuItem open = new MenuItem("Open", new MenuShortcut(KeyEvent.VK_O));
      open.addActionListener(new MenuAction());
      add(open);
      MenuItem save = new MenuItem("Save", new MenuShortcut(KeyEvent.VK_S));
      save.addActionListener(new MenuAction());
      add(save);
      MenuItem saveAs = new MenuItem("Save As", new MenuShortcut(KeyEvent.VK_S, true));
      saveAs.addActionListener(new MenuAction());
      add(saveAs);
      MenuItem export = new MenuItem("Export", new MenuShortcut(KeyEvent.VK_E));
      export.addActionListener(new MenuAction());
      add(export);
      MenuItem exportAs = new MenuItem("Export As", new MenuShortcut(KeyEvent.VK_E, true));
      exportAs.addActionListener(new MenuAction());
      add(exportAs);
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
