package ui;

import javax.swing.SwingUtilities;

/**
 * The Class Main.
 */
public class Main implements Runnable
{
    /** The instance. */
    public static MainWindow instance;

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args)
    {
      SwingUtilities.invokeLater(new Main());
      return;
    }

    /**
     * Instantiates a new main.
     * 
     * @param confObject
     *            the conf object
     */
    public Main()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        instance = new MainWindow();
    }
}
