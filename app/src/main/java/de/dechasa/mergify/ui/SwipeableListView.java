package de.dechasa.mergify.ui;

import android.content.Context;

public interface SwipeableListView {

    Context getContext();
    void deleteItem(int pos);
    void moveItem(int from, int to);
}
