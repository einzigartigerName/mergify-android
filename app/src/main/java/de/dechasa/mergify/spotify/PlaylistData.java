package de.dechasa.mergify.spotify;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.client.Response;

public class PlaylistData {

    private static final String REGEX_PLAYLIST_ID = "[a-z]{5}://[a-z]{4}\\.[a-z]{7}\\.[a-z]{3}/[a-z]{8}/([a-zA-Z0-9]+)\\?si";

    /**
     * Try extracting the Spotify Playlist ID for the given Link.
     * @param url full link
     * @return playlist id if found else null
     */
    public static String extractPlaylistID(String url) {
        Pattern pattern = Pattern.compile(REGEX_PLAYLIST_ID);

        Matcher matcher = pattern.matcher(url);

        if (matcher.find())
            return matcher.group(1);
        else
            return null;
    }

    public final String image;
    public final String name;
    public final String id;
    public final int total;

    public final List<TrackData> tracks;
    private SpotifyService spotify;

    public PlaylistData(String id, String name, String image, int total, String token) {
        this.image = image;
        this.name = name;
        this.id = id;
        this.total = total;

        tracks = new ArrayList<>();
        initSpotify(token);
    }

    /**
     * Append Tracks to List and try receiving missing tracks
     * @param tracks Tracks to add
     * @param userID UserID for the Call
     */
    public void setTracks(List<TrackData> tracks, String userID) {
        this.tracks.addAll(tracks);

        /* All Tracks received */
        if (this.tracks.size() >= total) {
            return;
        }

        Map<String, Object> options = new HashMap<>();
        options.put("offset", this.tracks.size());

        spotify.getPlaylistTracks(userID, this.id, options, new SpotifyCallback<Pager<PlaylistTrack>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("PlaylistData.class.Callback", "failure: receiving Tracks");
            }

            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                List<TrackData> tracks = playlistTrackPager.items
                        .stream()
                        .map(playlistTrack -> TrackData.fromTrack(playlistTrack.track))
                        .collect(Collectors.toList());

                setTracks(tracks, userID);
            }
        });
    }

    /**
     * Init a SpotifyService Object with the given token
     * @param token Access Token
     */
    private void initSpotify(String token) {
        spotify = new SpotifyApi()
                .setAccessToken(token)
                .getService();
    }
}
