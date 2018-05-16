package Modal;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hp on 5/10/2018.
 */

public class AreaLatLngEnt {

    LatLng latLng;
    String tweetDetail;
    String areaName;
    String random;

    public AreaLatLngEnt(LatLng latLng, String tweetDetail, String areaName, String random) {
        this.latLng = latLng;
        this.tweetDetail = tweetDetail;
        this.areaName = areaName;
        this.random = random;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTweetDetail() {
        return tweetDetail;
    }

    public void setTweetDetail(String tweetDetail) {
        this.tweetDetail = tweetDetail;
    }
}
