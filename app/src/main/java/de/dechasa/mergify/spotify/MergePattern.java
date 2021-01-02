package de.dechasa.mergify.spotify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum MergePattern {

    APPEND {
        /**
         * Alternate between Playlists when combining
         * @param playlists Playlists to Merge
         * @return Merged Tracks
         */
        @Override
        public List<TrackData> merge(List<List<TrackData>> playlists) {
            List<TrackData> combined = new ArrayList<>();
            playlists.forEach(combined::addAll);

            return combined;
        }

        @Override
        public String toString() {
            return "Append";
        }
    },


    SHUFFLE {
        /**
         * Shuffle Tracks
         * @param playlists Playlists to Merge
         * @return Merged Tracks
         */
        @Override
        public List<TrackData> merge(List<List<TrackData>> playlists) {
            List<TrackData> output = APPEND.merge(playlists);
            Collections.shuffle(output, new Random());

            return output;
        }

        @Override
        public String toString() {
            return "Shuffle";
        }
    },

    ALTERNATE {
        /**
         * Simply append the Playlists
         * @param playlists Playlists to Merge
         * @return Merged Tracks
         */
        @Override
        public List<TrackData> merge(List<List<TrackData>> playlists) {
            List<TrackData> output = new ArrayList<>();

            while (! playlists.isEmpty()) {
                List<TrackData> current = playlists.remove(0);
                if (!current.isEmpty()) {
                    output.add(current.remove(0));

                    if (!current.isEmpty()) {
                        playlists.add(current);
                    }
                }
            }

            return output;
        }

        @Override
        public String toString() {
            return "Alternate";
        }
    };

    /**
     * Merge Playlists with the current Routine
     * @param playlists Playlists to Merge
     * @return Merged Tracks
     */
    public abstract List<TrackData> merge(List<List<TrackData>> playlists);

    public static MergePattern fromString(String input) {
        switch (input.toLowerCase()) {
            case "append": return APPEND;
            case "shuffle": return SHUFFLE;
            case "alternate": return ALTERNATE;
            default: return null;
        }
    }
}
