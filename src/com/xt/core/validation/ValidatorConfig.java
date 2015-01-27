
package com.xt.core.validation;

/**
 *
 * @author albert
 */
public class ValidatorConfig {
    /**
     * 当前的编辑模式。
     */
    private EditMode editMode = EditMode.NONE;

    /**
     * 校验所属的组。
     */
    private String[] groups;

    public ValidatorConfig() {
    }

    public EditMode getEditMode() {
        return editMode;
    }

    public void setEditMode(EditMode editMode) {
        this.editMode = editMode;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

}
