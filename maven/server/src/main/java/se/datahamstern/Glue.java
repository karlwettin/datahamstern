package se.datahamstern;

import java.util.HashMap;

/**
 * global scope map
 * eg for putting data from bean shell between sessions and what not
 *
 * @author kalle
 * @since 2012-03-04 02:06
 */
public class Glue extends HashMap {

  private static Glue instance = new Glue();

  private Glue() {
  }

  public static Glue getInstance() {
    return instance;
  }

}
