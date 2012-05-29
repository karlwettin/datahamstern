package se.datahamstern.domain;

import com.sleepycat.persist.model.Persistent;
import se.datahamstern.sourced.AbstractSourced;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-05-24 12:21
 */
@Persistent(version = 1)
public class Koordinat implements Serializable {

  private static final long serialVersionUID = 1l;

  private double latitude;
  private double longitude;

  public Koordinat() {
  }

  public Koordinat(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public String toString() {
    return "Koordinat{" +
        "latitude=" + latitude +
        ", longitude=" + longitude +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Koordinat koordinat = (Koordinat) o;

    if (Double.compare(koordinat.latitude, latitude) != 0) return false;
    if (Double.compare(koordinat.longitude, longitude) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = latitude != +0.0d ? Double.doubleToLongBits(latitude) : 0L;
    result = (int) (temp ^ (temp >>> 32));
    temp = longitude != +0.0d ? Double.doubleToLongBits(longitude) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

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
