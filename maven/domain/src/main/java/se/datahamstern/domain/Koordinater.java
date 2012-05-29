package se.datahamstern.domain;

import com.sleepycat.persist.model.Persistent;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;
import java.util.List;

/**
 * @author kalle
 * @since 2012-05-29 12:17
 */
@Persistent(version = 1)
public class Koordinater implements Serializable {

  private static final long serialVersionUID = 1l;

  private SourcedValue<Koordinat> centroid = new SourcedValue<Koordinat>();
  private SourcedValue<List<Koordinat>> polygon = new SourcedValue<List<Koordinat>>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Koordinater that = (Koordinater) o;

    if (centroid != null ? !centroid.equals(that.centroid) : that.centroid != null) return false;
    if (polygon != null ? !polygon.equals(that.polygon) : that.polygon != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = centroid != null ? centroid.hashCode() : 0;
    result = 31 * result + (polygon != null ? polygon.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Koordinater{" +
        "centroid=" + centroid +
        ", polygon=" + polygon +
        '}';
  }

  public SourcedValue<Koordinat> getCentroid() {
    return centroid;
  }

  public void setCentroid(SourcedValue<Koordinat> centroid) {
    this.centroid = centroid;
  }

  public SourcedValue<List<Koordinat>> getPolygon() {
    return polygon;
  }

  public void setPolygon(SourcedValue<List<Koordinat>> polygon) {
    this.polygon = polygon;
  }
}
