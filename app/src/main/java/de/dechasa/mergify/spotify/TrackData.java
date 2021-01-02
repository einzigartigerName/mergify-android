package de.dechasa.mergify.spotify;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.stream.Collectors;

import kaaes.spotify.webapi.android.models.Track;

public class TrackData implements Parcelable {

    /**
     * Create a TrackData Object form Spotify Track
     * @param track Spotify Track
     * @return Constructed TrackData
     */
    public static TrackData fromTrack(Track track) {
        List<String> artists = track.artists.stream()
                .map(a -> a.name)
                .collect(Collectors.toList());

        String img = "";

        if (! track.album.images.isEmpty()) {
            img = track.album.images.get(0).url;
        }


        return new TrackData(track.id, track.name, artists, img);
    }

    public final String id;
    public final String title;
    public final List<String> artists;
    public final String image;

    public TrackData(String id, String title, List<String> artists, String image) {
        this.id = id;
        this.title = title;
        this.artists = artists;
        this.image = image;
    }

    protected TrackData(Parcel in) {
            id = in.readString();
            image = in.readString();
            title = in.readString();
            artists = in.createStringArrayList();
    }

    public static final Creator<TrackData> CREATOR = new Creator<TrackData>() {
        @Override
        public TrackData createFromParcel(Parcel in) {
            return new TrackData(in);
        }

        @Override
        public TrackData[] newArray(int size) {
            return new TrackData[size];
        }
    };

    public String toStringArtists() {
        return artists.stream().
                map(Object::toString).
                collect(Collectors.joining(", "));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(image);
        dest.writeString(title);
        dest.writeStringList(artists);
    }
}
