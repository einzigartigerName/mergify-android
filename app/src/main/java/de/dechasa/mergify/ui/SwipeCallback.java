package de.dechasa.mergify.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import de.dechasa.mergify.R;

public class SwipeCallback extends ItemTouchHelper.SimpleCallback {

    private final SwipeableListView  adapter;

    // Draw Delete Background
    private final Drawable icon;
    private final ColorDrawable background;

    public SwipeCallback(SwipeableListView adapter) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;

        icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_delete_white_24);
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public void onChildDraw(@NotNull Canvas canvas, @NotNull RecyclerView rv, RecyclerView.@NotNull ViewHolder holder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        super.onChildDraw(canvas, rv, holder, dX, dY, actionState, isCurrentlyActive);

        View itemView = holder.itemView;
        int bgCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        // Swiping to the left
        if (dX < 0) {
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(
                    itemView.getRight() + ((int) dX) - bgCornerOffset
                    , itemView.getTop()
                    , itemView.getRight()
                    , itemView.getBottom());

            background.draw(canvas);
            icon.draw(canvas);

        } else { // view is not swiped
            background.setBounds(0, 0, 0, 0);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int direction) {
        int pos = holder.getAdapterPosition();
        adapter.deleteItem(pos);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder holder, @NonNull RecyclerView.ViewHolder target) {
        int from = holder.getAdapterPosition();
        int to = target.getAdapterPosition();

        if (from != to) {
            adapter.moveItem(from, to);
            return true;
        }

        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int drag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipe = ItemTouchHelper.LEFT;

        return makeMovementFlags(drag, swipe);
    }
}
