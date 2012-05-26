package se.datahamstern.external.osm;

/**
 * @author kalle
 * @since 2012-05-24 12:51
 */
public class Node {

  private int id;

  private double latitude;
  private double longitude;

  @Override
  public String toString() {
    return "Node{" +
        "id=" + id +
        ", latitude=" + latitude +
        ", longitude=" + longitude +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Node node = (Node) o;

    if (id != node.id) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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
