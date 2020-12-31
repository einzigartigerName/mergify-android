package de.dechasa.mergify.ui.layout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.dechasa.mergify.R;
import de.dechasa.mergify.databinding.ActivityTracksBinding;
import de.dechasa.mergify.spotify.TrackData;
import de.dechasa.mergify.ui.SwipeCallback;
import de.dechasa.mergify.ui.TrackAdapter;
import de.dechasa.mergify.ui.dialog.CreatePlaylistDialog;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.client.Response;

public class TracksActivity extends AppCompatActivity implements CreatePlaylistDialog.OnClickListener{

    private static final String TAG = "TracksActivity.class";

    private TrackAdapter adapter;

    private SpotifyService spotify;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTracksBinding binding = ActivityTracksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Get Tracks from Intent */
        List<TrackData> tracks = getIntent()
                .getParcelableArrayListExtra(
                        getString(R.string.bundle_all_tracks));

        /* Init Spotify Connections */
        SharedPreferences storage = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        String token = storage.getString(getString(R.string.storage_token), "");

        userID = getIntent().getStringExtra(getString(R.string.bundle_spotify_userID));
        spotify = new SpotifyApi().setAccessToken(token).getService();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /* Init RecyclerView for Playlists */
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new TrackAdapter(this, tracks);

        binding.trackListView.setLayoutManager(lm);
        binding.trackListView.setAdapter(adapter);
        binding.trackListView.setItemAnimator(new DefaultItemAnimator());
        binding.trackListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        ItemTouchHelper swipeListener = new ItemTouchHelper(new SwipeCallback(adapter));
        swipeListener.attachToRecyclerView(binding.trackListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_track_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();
            return true;

        } else if (itemId == R.id.btnTracksSave) {
            onClickSave();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Get Playlist Options from Dialog and Create new Playlist
     * On Success -> Start Adding all Tracks
     * @param options Options to create Playlist from
     */
    @Override
    public void onPositive(Map<String, Object> options) {
        spotify.createPlaylist(userID, options, new SpotifyCallback<Playlist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getBaseContext(),  "createPlaylist.failure: " + spotifyError.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void success(Playlist playlist, Response response) {
                appendTracks(adapter.getTracks(), playlist.id, 0);
            }
        });
    }

    private void onClickSave() {
        DialogFragment dialog = new CreatePlaylistDialog();
        dialog.show(getSupportFragmentManager(), "CreatePlaylistDialog");
    }

    private void appendTracks(List<String> tracks, String playlist, int offset) {
        int toIndex = Math.min(offset + 100, tracks.size());

        List<String> subList = tracks.subList(offset, toIndex)
                .stream()
                .map(uri -> "spotify:track:" + uri)
                .collect(Collectors.toList());

        Map<String, Object> body = new HashMap<>();
        body.put("uris", subList);

        spotify.addTracksToPlaylist(userID, playlist, null, body, new SpotifyCallback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> pager, Response response) {
                if (tracks.size() > offset + 100) {
                    appendTracks(tracks, playlist, offset + 100);
                } else {
                    Toast.makeText(
                            getBaseContext(),
                            "Created Playlist",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getBaseContext(), "addTracksToPlaylist - failure: " + spotifyError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
