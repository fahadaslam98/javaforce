package javaforce.pi;

/** Raspberry Pi Test
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.webui.*;

public class Test implements WebUIHandler {
  public static void main(String args[]) {
    new Test().start();
  }
  public void start() {
    if (!GPIO.init()) {
      JFLog.log("GPIO.init() failed");
      return;
    }
    for(int a=0;a<8;a++) {
      GPIO.configInput(a);
      GPIO.configOutput(a+8);
    }
    initResources();
    Server server = new Server();
    server.start(this, 8080, false);
  }

  public Client client;
  public class TestPanel extends Panel {
    public Button b[];
    public Image i[];
  }
  public static TestPanel panel;
  public static Resource on, off;

  private void initResources() {
    on = Resource.readResource("javaforce/pi/on.png", Resource.PNG);
    off = Resource.readResource("javaforce/pi/off.png", Resource.PNG);
  }

  public Panel getRootPanel(Client client) {
    this.client = client;
    panel = new TestPanel();
    Column col = new Column();
    panel.add(col);
    panel.i = new Image[8];
    for(int a=0;a<7;a++) {
      Row row = new Row();
      col.add(row);
      row.add(new Pad());
      panel.i[a] = new Image(off);
      row.add(panel.i[a]);
      row.add(new Pad());
    }
    panel.b = new Button[8];
    for(int a=0;a<7;a++) {
      Row row = new Row();
      col.add(row);
      row.add(new Pad());
      panel.b[a] = new Button("O" + (a+1));
      final int idx = a;
      panel.b[a].addClickListener((Button b) -> {
        boolean state = !outputs[idx];
        outputs[idx] = state;
        GPIO.write(idx, state);
      });
      row.add(panel.b[a]);
      row.add(new Pad());
    }
    return panel;
  }

  public byte[] getResource(String url) {
    return null;
  }

  public static boolean outputs[] = new boolean[8];
  public static boolean inputs[] = new boolean[8];
  public static boolean display[] = new boolean[8];

  public static class Worker extends Thread {
    public volatile boolean active;

    public void run() {
      active = true;
      while (active) {
        //get inputs
        for(int a=0;a<8;a++) {
          inputs[a] = GPIO.read(a);
          if (inputs[a] != display[a]) {
            display[a] = inputs[a];
            panel.i[a].setImage(inputs[a] ? on : off);
          }
        }
        JF.sleep(1000);
      }
    }
  }
}