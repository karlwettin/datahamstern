package se.datahamstern.domain;

import com.sleepycat.persist.model.Persistent;

import java.util.List;

/**
 * @author kalle
 * @since 2012-05-29 11:41
 */
@Persistent(version = 1)
public class Geografi {

  private Koordinat centroid;
  private List<Koordinat> polygon;

}
