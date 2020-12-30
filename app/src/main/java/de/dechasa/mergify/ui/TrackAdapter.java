package de.dechasa.mergify.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.dechasa.mergify.R;
import de.dechasa.mergify.spotify.TrackData;

public class TrackAdapter extends RecyclerView.Adapter<GenericViewHolder> implements SwipeableListView {

    final Context context;
    final List<TrackData> tracks;

    public TrackAdapter(Context context, List<TrackData> tracks) {
        this.context = context;
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_generic, parent, false);

        return new GenericViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        TrackData data = tracks.get(position);

        holder.title.setText(data.title);
        holder.subtitle.setText(data.toStringArtists());

        Glide.with(context)
                .load(data.image)
                .fitCenter()
                .placeholder(R.drawable.ic_music_note_24)
                .into(holder.cover);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void deleteItem(int pos) {
        tracks.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public void moveItem(int from, int to) {
        Collections.swap(tracks, from, to);
        notifyItemMoved(from, to);
    }

    public List<String> getTracks() {
        return tracks.stream()
                .map(t -> t.id)
                .collect(Collectors.toList());
    }
}
