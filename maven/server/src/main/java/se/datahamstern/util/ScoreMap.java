package se.datahamstern.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * not thread safe!
 *
 * @author kalle
 * @since 2012-03-05 00:21
 */
public class ScoreMap<T> extends HashMap<T, Double> {

  public double increaseAndGet(T key) {
    return increaseAndGet(key, 1d);
  }

  public double increaseAndGet(T key, double amount) {
    Double value = get(key);
    if (value == null) {
      value = amount;
    } else {
      value += amount;
    }
    put(key, value);
    return value;
  }

  public void set(T key, double value) {
    put(key, value);
  }

  public Comparator<Map.Entry<T, Double>> comparator = new Comparator<Map.Entry<T, Double>>() {
    @Override
    public int compare(Map.Entry<T, Double> o1, Map.Entry<T, Double> o2) {
      return o2.getValue().compareTo(o2.getValue());
    }
  };

  public Map.Entry<T, Double>[] getHits() {
    Map.Entry<T, Double>[] hits = (Map.Entry<T, Double>[])entrySet().toArray(new Map[size()]);
    Arrays.sort(hits, comparator);
    return hits;
  }

}
