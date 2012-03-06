package se.datahamstern.domain;

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
