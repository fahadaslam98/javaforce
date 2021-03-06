/** Tapes
 *
 * Keeps track of tapes (by barcode)
 *
 * @author pquiring
 */

import java.util.*;
import java.io.*;
import javaforce.JFLog;

public class Tapes implements Serializable {
  private static final long serialVersionUID = 1L;

  public static Tapes current;

  private static Object lock = new Object();

  public ArrayList<EntryTape> tapes = new ArrayList<EntryTape>();

  public static void load() {
    try {
      synchronized(lock) {
        FileInputStream fis = new FileInputStream(Paths.dataPath + "/tapes.dat");
        ObjectInputStream ois = new ObjectInputStream(fis);
        current = (Tapes)ois.readObject();
        fis.close();
      }
    } catch (FileNotFoundException e) {
      current = new Tapes();
    } catch (Exception e) {
      current = new Tapes();
      JFLog.log(e);
    }
  }

  public static void save() {
    try {
      synchronized(lock) {
        FileOutputStream fos = new FileOutputStream(Paths.dataPath + "/tapes.dat");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(current);
        fos.close();
      }
    } catch (Exception e) {
      JFLog.log(e);
    }
  }

  public static void removeOldTapes(long now) {
    ArrayList<EntryTape> remove = new ArrayList<EntryTape>();
    synchronized(lock) {
      for(EntryTape tape : current.tapes) {
        if (tape.retention < now) {
          remove.add(tape);
        }
      }
      if (remove.size() == 0) return;
      for(EntryTape tape : remove) {
        current.tapes.remove(tape);
      }
    }
    save();
  }

  public static EntryTape findTape(String barcode) {
    synchronized(lock) {
      for(EntryTape tape : current.tapes) {
        if (tape.barcode.equals(barcode)) return tape;
      }
      if (barcode.startsWith(Config.current.cleanPrefix) || barcode.endsWith(Config.current.cleanSuffix)) {
        return new EntryTape(barcode, -1, -1, "n/a", -1);
      }
    }
    return null;
  }

  public static void deleteTapes(ArrayList<EntryTape> tapes) {
    synchronized(lock) {
      int cnt = 0;
      for(EntryTape delete : tapes) {
        for(EntryTape tape : current.tapes) {
          if (tape.barcode.equals(delete.barcode)) {
            current.tapes.remove(tape);
            cnt++;
            break;
          }
        }
      }
      if (cnt > 0) {
        save();
      }
    }
  }
}
