package de.dechasa.mergify.spotify;

import java.util.HashMap;
import java.util.Map;

public class PlaylistOptionBuilder {
    private String name;
    private String description = "";
    private boolean visible = true;
    private boolean collaborative = false;

    public PlaylistOptionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PlaylistOptionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public PlaylistOptionBuilder setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public PlaylistOptionBuilder setCollaborative(boolean collaborative) {
        this.collaborative = collaborative;
        return this;
    }

    public Map<String, Object> build() {
        HashMap<String, Object> options = new HashMap<>();

        options.put("name", this.name);
        options.put("public", this.visible);
        options.put("collaborative", this.collaborative);
        options.put("description", this.description);

        return  options;
    }
}
