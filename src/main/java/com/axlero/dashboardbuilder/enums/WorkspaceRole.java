package com.axlero.dashboardbuilder.enums;

/** A user's role inside one workspace. Declaration order is the privilege order (lowest first). */
public enum WorkspaceRole {
    VIEWER,
    EDITOR,
    OWNER;

    public boolean atLeast(WorkspaceRole required) {
        return this.ordinal() >= required.ordinal();
    }
}
