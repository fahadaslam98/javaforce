package jfcontrols.tags;

/**
 *
 * @author User
 */

public class TagString extends TagBase {
  public TagString() {}
  public TagString(int cid, int tid, String name, int length) {
    type = javaforce.controls.TagType.string;
    this.cid = cid;
    this.tid = tid;
    this.name = name;
    this.unsigned = unsigned;
    isArray = true;
    if (length == 0) length = 1;
    values = new char[length][0];
  }
  public char[][] values;
  public String toString(int idx) {return new String(values[idx]);}
  public int getLength() {return values[0].length;}
  public boolean getBoolean(int idx) {
    return toString(idx).equals("true");
  }

  public void setBoolean(int idx, boolean value) {
    setString8(idx, value ? "true" : "false");
  }

  public int getInt(int idx) {
    return Integer.valueOf(toString(idx));
  }

  public void setInt(int idx, int value) {
    setString8(idx, Integer.toString(value));
  }

  public long getLong(int idx) {
    return Long.valueOf(toString(idx));
  }

  public void setLong(int idx, long value) {
    setString8(idx, Long.toString(value));
  }

  public float getFloat(int idx) {
    return Float.valueOf(toString(idx));
  }

  public void setFloat(int idx, float value) {
    setString8(idx, Float.toString(value));
  }

  public double getDouble(int idx) {
    return Double.valueOf(toString(idx));
  }

  public void setDouble(int idx, double value) {
    setString8(idx, Double.toString(value));
  }

  public String getString8() {
    return toString(0);
  }

  public void setString8(int idx, String str) {
    values[idx] = str.toCharArray();
  }

  public void readObject() throws Exception {
    super.readObject();
    int cnt = readInt();
    values = new char[cnt][];
    for(int a=0;a<cnt;a++) {
      values[a] = readString().toCharArray();
    }

  }
  public void writeObject() throws Exception {
    super.writeObject();
    int cnt = values.length;
    writeInt(cnt);
    for(int a=0;a<cnt;a++) {
      writeString(new String(values[a]));
    }
  }
}
