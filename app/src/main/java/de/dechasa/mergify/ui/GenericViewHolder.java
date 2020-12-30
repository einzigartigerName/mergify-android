package de.dechasa.mergify.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.dechasa.mergify.R;

public class GenericViewHolder extends RecyclerView.ViewHolder {

    final ImageView cover;
    final TextView title;
    final TextView subtitle;

    public GenericViewHolder(@NonNull View view) {
        super(view);

        cover = view.findViewById(R.id.imgItemCover);
        title = view.findViewById(R.id.txtItemTitle);
        subtitle = view.findViewById(R.id.txtItemSubtitle);
    }

    public ImageView getCover() {
        return cover;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getSubtitle() {
        return subtitle;
    }
}
