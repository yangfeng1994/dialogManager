package com.dialog.queue;


public interface DialogController {
    default String getTagName(){
        return null;
    }

    default boolean unique() {
        return false;
    }
}
