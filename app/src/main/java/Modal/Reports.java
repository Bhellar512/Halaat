package Modal;

/**
 * Created by Dell on 12/4/2017.
 */

public class Reports {

    String userLocation;
    String userId;
    String userComments;
    String reportType;
    Double latitude;
    Double longitude;

    public Reports() {
    }

    public Reports(String userLoc, String userId, String userComments, String type, Double lat, Double longi) {
        this.userComments = userComments;
        this.userId= userId;
        this.reportType = type;
        this.userLocation = userLoc;
        this.latitude = lat;
        this.longitude = longi;
    }

    public String getUserLocation() {
        return userLocation;
    }
    public String getUserId() {
        return userId;
    }
    public String getUserComments() {
        return userComments;
    }
    public String getReportType() {
        return reportType;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() { return longitude; }

}
