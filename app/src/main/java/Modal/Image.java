package Modal;

/**
 * Created by Dell on 12/4/2017.
 */

public class Image {
    String uri;
    String description;


    public Image(String uri, String description) {
        this.uri = uri;
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
