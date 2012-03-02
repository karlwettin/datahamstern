package se.datahamstern.services.naringslivsregistret;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kalle
 * @since 2012-02-29 03:33
 */
public class OrganizationTypeFilter extends NaringslivsregistretResultsFilter {

  private Set<String> acceptedTypes;

  public OrganizationTypeFilter(String... acceptedTypes) {
    this.acceptedTypes = new HashSet<String>();
    for (String type : acceptedTypes) {
      this.acceptedTypes.add(type);
    }
  }

  @Override
  public List<NaringslivsregistretResult> filter(List<NaringslivsregistretResult> input) {
    List<NaringslivsregistretResult> results = new ArrayList<NaringslivsregistretResult>(input.size());
    for (NaringslivsregistretResult result : input) {
      if (acceptedTypes.contains(result.getType())) {
        results.add(result);
      }
    }
    return results;
  }

  public Set<String> getAcceptedTypes() {
    return acceptedTypes;
  }

  public void setAcceptedTypes(Set<String> acceptedTypes) {
    this.acceptedTypes = acceptedTypes;
  }
}
