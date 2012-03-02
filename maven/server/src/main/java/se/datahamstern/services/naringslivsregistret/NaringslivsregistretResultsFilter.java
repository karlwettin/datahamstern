package se.datahamstern.services.naringslivsregistret;

import java.util.List;

/**
 * @author kalle
 * @since 2012-02-29 03:32
 */
public abstract class NaringslivsregistretResultsFilter {

  public abstract List<NaringslivsregistretResult> filter(List<NaringslivsregistretResult> input);

}
