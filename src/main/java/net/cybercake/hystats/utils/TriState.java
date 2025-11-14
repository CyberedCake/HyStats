package net.cybercake.hystats.utils;

public enum TriState {

    TRUE,

    FALSE,

    UNSET;

    public boolean bool() {
        return this == TRUE;
    }

    public static TriState from(boolean bool) {
        return bool ? TRUE : FALSE;
    }

}
