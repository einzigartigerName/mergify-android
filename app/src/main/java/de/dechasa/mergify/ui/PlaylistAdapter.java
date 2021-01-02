package de.dechasa.mergify.ui;

import android.content.Context;
import android.graphics.Typeface;
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
import de.dechasa.mergify.spotify.PlaylistData;
import de.dechasa.mergify.spotify.TrackData;

public class PlaylistAdapter extends RecyclerView.Adapter<GenericViewHolder>  implements SwipeableListView {

    final Context context;
    final List<PlaylistData> playlists;

    public PlaylistAdapter(Context context, List<PlaylistData> playlists) {
        this.context = context;
        this.playlists = playlists;
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
        PlaylistData data = playlists.get(position);

        String total = "Total: " + data.total;

        holder.title.setText(data.name);
        holder.subtitle.setText(total);

        Glide.with(context)
                .load(data.image)
                .fitCenter()
                .placeholder(R.drawable.ic_library_music_24)
                .into(holder.cover);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void deleteItem(int pos) {
        playlists.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public void moveItem(int from, int to) {
        Collections.swap(playlists, from, to);
        notifyItemMoved(from, to);
    }

    /**
     * Add PlaylistData to List
     * @param playlist Playlist to add
     */
    public void addPlaylist(PlaylistData playlist) {
        playlists.add(playlist);
        notifyDataSetChanged();
    }

    /**
     * Return all Tracks outside of Playlist Object
     * @return all Tracks
     */
    public List<List<TrackData>> getPlaylistTracks() {
        return playlists.stream()
                .map(playlist -> playlist.tracks)
                .collect(Collectors.toList());
    }
}
