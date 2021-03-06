 package javaforce.webui;

/** WebUI Server.
 *
 * WebUI uses a WebSocket to communicate between the server and client side to create
 * rich interfaces that are programmed like standard desktop applications.
 * No ugly javascript, css or html knowledge needed.
 * WebUI is not dependant on AWT or any related APIs and should work with the Java base module.
 *
 * Browser support:
 *
 *    Internet Explorer is NOT supported
 *    Edge 12+
 *    FireFox 28+
 *    Chrome 21+
 *
 * @author pquiring
 */

import java.io.*;
import java.util.*;

import javaforce.*;
import javaforce.service.*;

public class WebUIServer implements WebHandler, WebSocketHandler {
  private Web web;
  private WebUIHandler handler;

  /** Enable debug log messages. */
  public static boolean debug = false;

  /** Enable transaction id in WebSocket messages for debugging. */
  public static boolean debug_tid = false;

  public void start(WebUIHandler handler, int port, boolean secure) {
    this.handler = handler;
    if (web != null) stop();
    web = new Web();
    web.setWebSocketHandler(this);
    web.start(this, port, secure);
    System.out.println("WebUI Server listing on port " + port);
  }

  public void stop() {
    if (web != null) {
      web.stop();
      web = null;
    }
  }

  public byte[] getResource(String name) {
    InputStream is = getClass().getClassLoader().getResourceAsStream(name);
    if (is == null) {
      return null;
    }
    return JF.readAll(is);
  }

  public void doPost(WebRequest req, WebResponse res) {
    doGet(req, res);
  }

  public void doGet(WebRequest req, WebResponse res) {
    String url = req.getURL();
    byte data[] = null;
/*
    if (url.endsWith(".html")) {
      res.setContentType("text/html");  //default
    }
*/
    if (url.endsWith(".css")) {
      res.setContentType("text/css");
    }
    if (url.endsWith(".png")) {
      res.setContentType("image/png");
    }
    if (url.endsWith(".js")) {
      res.setContentType("text/javascript");
    }
    if (url.equals("/")) {
      url = "/webui.html";
      String browser = req.getHeader("User-Agent");
      if (browser != null) {
        if (browser.indexOf("Trident") != -1) {
          //Internet Explorer - not supported
          url = "/webui-ie.html";
        }
      }
    }
    if (url.startsWith("/static/")) {
      // url = /static/r#
      Resource r = Resource.getResource(url.substring(8));
      if (r != null) {
        data = r.data;
        res.setContentType(r.mime);
      }
    } else if (url.startsWith("/user/")) {
      data = handler.getResource(url);
      res.addHeader("Cache-Control: no-store");
    } else {
      data = getResource("javaforce/webui/static" + url);
    }
    if (data == null) {
      res.setStatus(404, "Resource not found");
      return;
    }
    try {
      res.write(data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ArrayList<WebUIClient> clients = new ArrayList<WebUIClient>();

  public WebUIClient getClient(WebSocket sock) {
    int cnt = clients.size();
    for(int a=0;a<cnt;a++) {
      WebUIClient client = clients.get(a);
      if (client.socket == sock) {
        return client;
      }
    }
    return null;
  }

  public WebUIClient getClient(String hash) {
    int cnt = clients.size();
    for(int a=0;a<cnt;a++) {
      WebUIClient client = clients.get(a);
      if (client.hash.equals(hash)) {
        return client;
      }
    }
    return null;
  }

  public boolean doWebSocketConnect(WebSocket sock) {
    try {
      WebUIClient client = new WebUIClient(sock, handler);
      clients.add(client);
      handler.clientConnected(client);
      return true;
    } catch (Exception e) {
      JFLog.log(e);
      return false;
    }
  }

  public void doWebSocketClosed(WebSocket sock) {
    WebUIClient client = getClient(sock);
    clients.remove(client);
    handler.clientDisconnected(client);
  }

  public void doWebSocketMessage(WebSocket sock, byte[] data, int type) {
    WebUIClient client = getClient(sock);
    if (client == null) {
      JFLog.log("Unknown Socket:" + sock);
      return;
    }
    String msg = new String(data);
    if (debug) JFLog.log("RECV=" + msg);
    //decode JSON
    JSON.Element json;
    try {
      json = JSON.parse(msg);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    String id = "";
    String event = "";
    ArrayList<String> args = new ArrayList<String>();
    int cnt = json.children.size();
    for(int a=0;a<cnt;a++) {
      JSON.Element e = json.children.get(a);
      if (e.key.equals("id")) {
        id = e.value;
      }
      else if (e.key.equals("event")) {
        event = e.value;
      }
      else {
        args.add(e.key + "=" + e.value);
      }
    }
    try {
      client.dispatchEvent(id, event, args.toArray(new String[args.size()]));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
