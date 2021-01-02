package de.dechasa.mergify.ui.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.dechasa.mergify.R;
import de.dechasa.mergify.databinding.ActivityMainBinding;
import de.dechasa.mergify.spotify.MergePattern;
import de.dechasa.mergify.spotify.PlaylistData;
import de.dechasa.mergify.spotify.TrackData;
import de.dechasa.mergify.ui.PlaylistAdapter;
import de.dechasa.mergify.ui.SwipeCallback;
import de.dechasa.mergify.ui.dialog.AddPlaylistDialog;
import de.dechasa.mergify.ui.dialog.MergePatternDialog;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements
        AddPlaylistDialog.OnClickListener,
        MergePatternDialog.OnClickListener {

    private static final String TAG = "MainActivity.class";

    private PlaylistAdapter adapter;
    private SpotifyService spotify;
    private Menu menu;

    private String userID;
    private String TOKEN;

    private MergePattern pattern = MergePattern.APPEND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Get Token from Storage */
        SharedPreferences storage = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        TOKEN = storage.getString(getString(R.string.storage_token), "");

        /* HomeAsUp Button as Profile Button */
        spotify = new SpotifyApi().setAccessToken(TOKEN).getService();
        spotify.getMe(spotifyCallbackMe);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /* Init RecyclerView for Playlists */
        // List<PlaylistData> data = createSampleData();
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new PlaylistAdapter(this, new ArrayList<>());

        binding.playlistListView.setLayoutManager(lm);
        binding.playlistListView.setAdapter(adapter);
        binding.playlistListView.setItemAnimator(new DefaultItemAnimator());
        binding.playlistListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper swipeListener = new ItemTouchHelper(new SwipeCallback(adapter));
        swipeListener.attachToRecyclerView(binding.playlistListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            return true;

        } else if (itemId == R.id.btnMainContinue) {
            onClickContinue();
            return true;

        } else if (itemId == R.id.btnMergePatternSelect) {
            onClickMergeSelect();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * AddPlaylistDialog Callback
     * @param playlist playlist to Add to List
     */
    @Override
    public void onPositive(String playlist) {
        spotify.getPlaylist(userID, playlist, new SpotifyCallback<Playlist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getBaseContext(),  "failure: Getting Playlist " + playlist, Toast.LENGTH_LONG).show();
            }

            @Override
            public void success(Playlist playlist, Response response) {
                String id = playlist.id;
                String name = playlist.name;
                String img = playlist.images.get(0).url;
                int total = playlist.tracks.total;
                List<TrackData> tracks = playlist.tracks
                        .items
                        .stream()
                        .map(playlistTrack -> TrackData.fromTrack(playlistTrack.track))
                        .collect(Collectors.toList());

                PlaylistData data = new PlaylistData(id, name, img, total, TOKEN);

                adapter.addPlaylist(data);

                data.setTracks(tracks, userID);
            }
        });
    }

    /**
     * MergePatternSelect Callback
     * -> Save selected and update Icon for MenuButton
     * @param pattern selected Pattern
     */
    @Override
    public void onSave(MergePattern pattern) {
        if (pattern != null) {
            this.pattern = pattern;
            setMergePatternButton();
        }
    }

    /**
     * Show Dialog for Adding a new Playlist
     */
    public void onClickAdd(final View view) {
        DialogFragment dialog = new AddPlaylistDialog();
        dialog.show(getSupportFragmentManager(), "AddPlaylistDialog");
    }

    /**
     * Show the Merge Select Dialog
     */
    private void onClickMergeSelect() {
        MergePatternDialog dialog = new MergePatternDialog(pattern);
        dialog.show(getSupportFragmentManager(), "MergePatternDialog");
    }

    /**
     * Continue to TrackActivity for editing the Tracks
     */
    private void onClickContinue() {
        List<List<TrackData>> playlists = adapter.getPlaylistTracks();

        playlists.forEach(l -> l.forEach(t -> System.out.println(t.title)));


        ArrayList<TrackData> allData = new ArrayList<>(pattern.merge(playlists));

        Intent activity = new Intent(this, TracksActivity.class);
        activity.putParcelableArrayListExtra(getString(R.string.bundle_all_tracks), allData);
        activity.putExtra(getString(R.string.bundle_spotify_userID), userID);

        startActivity(activity);
    }

    /**
     * Set the Icon for the MenuItem select MergePattern Button
     * Default: APPEND
     */
    private void setMergePatternButton() {
        int drawableID;

        switch (pattern) {
            case SHUFFLE:
                drawableID = R.drawable.ic_shuffle_24;
                break;

            case ALTERNATE:
                drawableID = R.drawable.ic_alternate_24;
                break;
            default:
                pattern = MergePattern.APPEND;
                drawableID = R.drawable.ic_append_24;
                break;
        }

        menu.findItem(R.id.btnMergePatternSelect).setIcon(drawableID);
    }

    /**
     * Callback for getting the Current Users Data + Start Downloading his profile Picture
     */
    private final SpotifyCallback<UserPrivate> spotifyCallbackMe = new SpotifyCallback<UserPrivate>() {
        @Override
        public void failure(SpotifyError spotifyError) {
            Toast.makeText(getBaseContext(), "User - failure: " + spotifyError.getMessage(), Toast.LENGTH_LONG)
                .show();
        }

        @Override
        public void success(UserPrivate userPrivate, Response response) {
            String img = (userPrivate.images.isEmpty()) ? "" : userPrivate.images.get(0).url;

            userID = userPrivate.id;

            Glide.with(getBaseContext())
                    .asDrawable()
                    .load(img)
                    .placeholder(R.drawable.ic_profile_24)
                    .fitCenter()
                    .circleCrop()
                    .into(homeIndicatorTarget);
        }
    };

    /**
     * Glide Target for setting the HomeAsUpIndicator on the ActionBar to the downloaded Image
     */
    private final CustomTarget<Drawable> homeIndicatorTarget = new CustomTarget<Drawable>() {
        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            Bitmap b = ((BitmapDrawable) resource).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 128, 128, false);
            Drawable img = new BitmapDrawable(getResources(), bitmapResized);


            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(img);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(placeholder);
        }
    };
}