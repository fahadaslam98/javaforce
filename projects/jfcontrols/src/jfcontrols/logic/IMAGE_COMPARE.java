package jfcontrols.logic;

/** Image Compare
 *
 * @author pquiring
 */

import javaforce.controls.*;
import jfcontrols.tags.*;

public class IMAGE_COMPARE extends LogicBlock {

  public boolean isBlock() {
    return true;
  }

  public String getDesc() {
    return "ImageCompare";
  }

  public boolean execute(boolean enabled) {
    //TODO
    return enabled;
  }

  public int getTagsCount() {
    return 3;  //program# shot# roi#
  }

  public int getTagType(int idx) {
    return TagType.uint32;
  }
}
