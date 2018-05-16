package Modal;

/**
 * Created by Hp on 5/10/2018.
 */

public class TweetsAreaEnt {

    String areaName;
    String tweetDetail;

    public TweetsAreaEnt(String areaName, String tweetDetail) {
        this.areaName = areaName;
        this.tweetDetail = tweetDetail;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getTweetDetail() {
        return tweetDetail;
    }

    public void setTweetDetail(String tweetDetail) {
        this.tweetDetail = tweetDetail;
    }
}
