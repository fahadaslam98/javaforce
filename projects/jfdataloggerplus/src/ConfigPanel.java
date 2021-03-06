/** Config Panel
 *
 * @author pquiring
 */

import javaforce.webui.*;
import javaforce.controls.*;

public class ConfigPanel extends CenteredPanel {
  public ConfigPanel() {
    Row row;

    row = new Row();
    add(row);
    ltags = new Label("Tags");
    row.add(ltags);
    tags = new ComboBox();
    tags.setWidth(250);
    row.add(tags);

    loadTags();

    add = new Button("Add");
    add.setFontSize(24);
    add(add);
    edit = new Button("Edit");
    edit.setFontSize(24);
    add(edit);
    delete = new Button("Delete");
    delete.setFontSize(24);
    add(delete);

    back = new Button("Back");
    back.setFontSize(24);
    add(back);

    confirmDelete = new MessagePopup("Delete Tag?", "Are you sure?", true);
    add(confirmDelete);

    ccp = new ColorChooserPopup();
    add(ccp);

    editTag = new EditTagPopup(ccp);
    add(editTag);

    add.addClickListener((me, c) -> {
      newTag = true;
      editTag.newTag();
    });
    edit.addClickListener((me, c) -> {
      int idx = tags.getSelectedIndex();
      if (idx == -1) return;
      newTag = false;
      editingTag = list[idx];
      editTag.editTag(editingTag);
    });
    editTag.addValidateListener((c) -> {
      return !Service.exists(editTag.getHost(), editTag.getTag());
    });
    delete.addClickListener((me, c) -> {
      int idx = tags.getSelectedIndex();
      if (idx == -1) return;
      confirmDelete.setVisible(true);
    });
    back.addClickListener((me, c) -> {
      c.getClient().setPanel(new MainPanel());
    });
    confirmDelete.addActionListener((c) -> {
      int idx = tags.getSelectedIndex();
      if (idx == -1) return;
      Service.removeTag(list[idx]);
      loadTags();
    });
    editTag.addActionListener((c) -> {
      if (newTag) {
        Tag new_Tag = new Tag();
        editTag.saveTag(new_Tag);
        Service.addTag(new_Tag);
      } else {
        Service.updateTag(editingTag);
      }
      loadTags();
    });
  }
  public Button back;
  public Label ltags;
  public ComboBox tags;
  public Button add, edit, delete;
  public MessagePopup confirmDelete;
  public ColorChooserPopup ccp;
  public EditTagPopup editTag;

  private boolean newTag;
  private Tag editingTag;

  private Tag list[];

  public void loadTags() {
    list = Service.getTags();
    tags.clear();
    for(int a=0;a<list.length;a++) {
      String url = list[a].toString();
      tags.add("i" + a, url);
    }
  }
}
