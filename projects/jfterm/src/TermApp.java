/*
 * TermApp.java
 *
 * Created on July 31, 2007, 8:26 AM
 *
 * @author  pquiring
 *
 * Requires JSch from www.jcraft.com (which requires JZLib)
 *
 * Known Bug : CTRL+TAB/CTRL+SHIFT+TAB are still captured by JTabbedPane.
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javaforce.*;

public class TermApp extends javax.swing.JFrame implements KeyEventDispatcher {

  public static String version = "0.19";

  public boolean dispatchKeyEvent(KeyEvent e) {
    //System.out.println("KeyEvent:" + e);
    if ((e.getSource() instanceof Buffer) && (e.getKeyCode() == e.VK_TAB) && (e.getModifiers() == 0)) {
      return true;  //do not pass on to next dispatch handler (prevents FocusManager from using TAB to switch tabs)
    }
    return false;  //pass on as normal
  }

  /** Creates new form TermApp */
  public TermApp() {
    initComponents();
    //disable TAB processing for Buffer
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    Settings.loadSettings();
    setSize(Settings.settings.WindowXSize, Settings.settings.WindowYSize);
    setLocation(Settings.settings.WindowXPos, Settings.settings.WindowYPos);
    if (Settings.settings.bWindowMax) setExtendedState(MAXIMIZED_BOTH);
    Menu.create(this, tabs, !JF.isWindows());
    if (!JF.isWindows()) {
      Menu.localAction();
    }
    setTitle("jfTerm/" + version);
    JFImage icon = new JFImage();
    icon.loadPNG(this.getClass().getClassLoader().getResourceAsStream("jfterm.png"));
    setIconImage(icon.getImage());
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    tabs = new javax.swing.JTabbedPane();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("jfterm");
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentMoved(java.awt.event.ComponentEvent evt) {
        formComponentMoved(evt);
      }
      public void componentResized(java.awt.event.ComponentEvent evt) {
        formComponentResized(evt);
      }
    });
    addWindowStateListener(new java.awt.event.WindowStateListener() {
      public void windowStateChanged(java.awt.event.WindowEvent evt) {
        formWindowStateChanged(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
//    System.out.println("moved");
    if (Settings.settings.bWindowMax) return;
    Point loc = getLocation();
    Settings.settings.WindowXPos = loc.x;
    Settings.settings.WindowYPos = loc.y;
  }//GEN-LAST:event_formComponentMoved

  private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
//    System.out.println("resized");
    if (!Settings.settings.bWindowMax) {
      Dimension size = getSize();
      Settings.settings.WindowXSize = size.width;
      Settings.settings.WindowYSize = size.height;
    }
    //resize all Buffers
    Buffer buffer;
    for(int a=0;a<tabs.getTabCount();a++) {
      buffer = (Buffer)((JComponent)tabs.getComponentAt(a)).getClientProperty("buffer");
      buffer.changeSize();
    }
  }//GEN-LAST:event_formComponentResized

  private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
    Settings.settings.bWindowMax = evt.getNewState() == MAXIMIZED_BOTH;
  }//GEN-LAST:event_formWindowStateChanged

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    exit();
  }//GEN-LAST:event_formWindowClosing

  /**
   * @param args the command line arguments (currently ignored)
   */
  public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new TermApp().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTabbedPane tabs;
  // End of variables declaration//GEN-END:variables

  public void exit() {
    Settings.saveSettings();
    while(tabs.getTabCount() > 0) {
      Buffer buffer = (Buffer)((JComponent)tabs.getComponentAt(0)).getClientProperty("buffer");
      buffer.close();
    }
    System.exit(0);
  }
  public void showMaps(JComponent c) {
    System.out.println("Registered KeyStrokes for : " + c);
    KeyStroke ks[] = c.getRegisteredKeyStrokes();
    if (ks != null) {
      for(int a=0;a<ks.length;a++) System.out.println("KeyStroke : " + ks[a]);
    }
    showMaps(c,JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    showMaps(c,JComponent.WHEN_FOCUSED);
    showMaps(c,JComponent.WHEN_IN_FOCUSED_WINDOW);
  }
  public void showMaps(JComponent c, int cond) {
    System.out.println("Maps for : " + c + " : cond = " + cond);
    ActionMap m = tabs.getActionMap();
    do {
      System.out.println("amap = " + m);
      Object keys[] = m.allKeys();
      m = m.getParent();
      if (keys == null) {System.out.println("keys=null"); continue;}
      for(int a=0;a<keys.length;a++) { System.out.println("key:" + keys[a]); }
    } while (m != null);
    InputMap i = tabs.getInputMap(cond);
    do {
      System.out.println("imap = " + i);
      Object keys[] = i.allKeys();
      i = i.getParent();
      if (keys == null) {System.out.println("keys=null"); continue;}
      for(int a=0;a<keys.length;a++) { System.out.println("key:" + keys[a]); }
    } while (i != null);
    System.out.println("");
  }
}
