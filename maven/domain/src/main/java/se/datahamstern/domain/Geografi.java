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
public class Geografi implements Serializable {

  private static final long serialVersionUID = 1l;

  private SourcedValue<Koordinat> centroid = new SourcedValue<Koordinat>();
  private SourcedValue<List<Koordinat>> polygon = new SourcedValue<List<Koordinat>>();
  private SourcedValue<List<Double>> kvadratkilometerLandareal = new SourcedValue<List<Double>>();
  private SourcedValue<List<Double>> kvadratkilometerVattenareal = new SourcedValue<List<Double>>();

  @Override
  public String toString() {
    return "Geografi{" +
        "centroid=" + centroid +
        ", polygon=" + polygon +
        ", kvadratkilometerLandareal=" + kvadratkilometerLandareal +
        ", kvadratkilometerVattenareal=" + kvadratkilometerVattenareal +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Geografi geografi = (Geografi) o;

    if (centroid != null ? !centroid.equals(geografi.centroid) : geografi.centroid != null) return false;
    if (kvadratkilometerLandareal != null ? !kvadratkilometerLandareal.equals(geografi.kvadratkilometerLandareal) : geografi.kvadratkilometerLandareal != null) return false;
    if (kvadratkilometerVattenareal != null ? !kvadratkilometerVattenareal.equals(geografi.kvadratkilometerVattenareal) : geografi.kvadratkilometerVattenareal != null) return false;
    if (polygon != null ? !polygon.equals(geografi.polygon) : geografi.polygon != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = centroid != null ? centroid.hashCode() : 0;
    result = 31 * result + (polygon != null ? polygon.hashCode() : 0);
    result = 31 * result + (kvadratkilometerLandareal != null ? kvadratkilometerLandareal.hashCode() : 0);
    result = 31 * result + (kvadratkilometerVattenareal != null ? kvadratkilometerVattenareal.hashCode() : 0);
    return result;
  }

  public SourcedValue<List<Double>> getKvadratkilometerLandareal() {
    return kvadratkilometerLandareal;
  }

  public void setKvadratkilometerLandareal(SourcedValue<List<Double>> kvadratkilometerLandareal) {
    this.kvadratkilometerLandareal = kvadratkilometerLandareal;
  }

  public SourcedValue<List<Double>> getKvadratkilometerVattenareal() {
    return kvadratkilometerVattenareal;
  }

  public void setKvadratkilometerVattenareal(SourcedValue<List<Double>> kvadratkilometerVattenareal) {
    this.kvadratkilometerVattenareal = kvadratkilometerVattenareal;
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
