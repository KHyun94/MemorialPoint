package com.example.memorialpoint.Util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

public class CustomPopUpMenu {

    Context context;
    View v;
    PopupMenu popupMenu;

    public CustomPopUpMenu(Context context, View v) {
        this.context = context;
        this.v = v;
    }

    public PopupMenu showPopUp(int layout_menu) {
        popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(layout_menu, popupMenu.getMenu());

        return popupMenu;
    }

}
