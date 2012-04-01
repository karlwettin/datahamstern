package se.datahamstern.domain.hydda;

import com.sleepycat.persist.model.Persistent;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2012-03-05 05:12
 */
@Persistent(version = 1)
public class Dokumentversionskalla extends AbstractSourced implements Serializable {

  private static final long serialVersionUID = 1l;


  private String uri;
  private List<SourcedValue<String>> innehållsbeskrivningar = new ArrayList<SourcedValue<String>>();

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Dokumentversionskalla that = (Dokumentversionskalla) o;

    if (innehållsbeskrivningar != null ? !innehållsbeskrivningar.equals(that.innehållsbeskrivningar) : that.innehållsbeskrivningar != null) return false;
    if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (uri != null ? uri.hashCode() : 0);
    result = 31 * result + (innehållsbeskrivningar != null ? innehållsbeskrivningar.hashCode() : 0);
    return result;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public List<SourcedValue<String>> getInnehållsbeskrivningar() {
    return innehållsbeskrivningar;
  }

  public void setInnehållsbeskrivningar(List<SourcedValue<String>> innehållsbeskrivningar) {
    this.innehållsbeskrivningar = innehållsbeskrivningar;
  }
}
