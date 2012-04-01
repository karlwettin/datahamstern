package se.datahamstern.sourced;

import com.sleepycat.persist.model.Persistent;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kalle
 * @since 2012-04-01 13:35
 */
@Persistent(version = 1)
public class Sources implements Serializable {

  private static final long serialVersionUID = 1l;

  private Set<String> licenses = new HashSet<String>();
  private Set<String> authors = new HashSet<String>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Sources sources = (Sources) o;

    if (authors != null ? !authors.equals(sources.authors) : sources.authors != null) return false;
    if (licenses != null ? !licenses.equals(sources.licenses) : sources.licenses != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = licenses != null ? licenses.hashCode() : 0;
    result = 31 * result + (authors != null ? authors.hashCode() : 0);
    return result;
  }

  public Set<String> getLicenses() {
    return licenses;
  }

  public void setLicenses(Set<String> licenses) {
    this.licenses = licenses;
  }

  public Set<String> getAuthors() {
    return authors;
  }

  public void setAuthors(Set<String> authors) {
    this.authors = authors;
  }
}
