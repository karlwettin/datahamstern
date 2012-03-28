package se.datahamstern.domain;


/**
 * @author kalle
 * @since 2012-03-26 02:20
 */
public class GeographicCoordinate {

  private double latitude;
  private double longitude;

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}
