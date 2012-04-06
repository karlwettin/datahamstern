package se.datahamstern.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author kalle
 * @since 2012-04-06 15:55
 */
public class ListMap<K, V> extends HashMap<K, List<V>> {

  public void listAdd(K k, V v) {
    List<V> vs = get(k);
    if (vs == null) {
      put(k, new ArrayList<V>());
      vs = get(k);
    }
    vs.add(v);
  }

  public boolean listRemove(K k, V v) {
    List<V> vs = get(k);
    return vs != null && vs.remove(v);
  }

}
