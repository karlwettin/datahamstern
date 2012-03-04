package se.datahamstern.command;

import com.sleepycat.persist.model.Persistent;

import java.util.Date;

/**
 * exempelpost:
 *
 * "sources" : [{
 *   "author" : "janne1965@wikipedia.se",
 *   "trustworthiness" : 0.89,
 *   "details" : "ingen har ändrat på artikeln sedan tre månader då janne1965 städade efter ett troll",
 *   "license" : "public domain",
 *   "timestamp" : 1221112888483
 * }, {
 *   "author" : "nominatim@open street map",
 *   "trustworthiness" : 0.4,
 *   "details" : "sökte upp 'stockholms län' med open street maps geocoder och fick en rektangel  som polygonsvar",
 *   "license" : "creative commons",
 *   "timestamp" : 1221112943233
 * }]
 *
 *
 * @author kalle
 * @since 2012-03-03 22:15
 */
@Persistent(version = 1)
public class Source {

  /**
   * exempelvis "janne1965@wikipedia/se", en url till ett dokument, etc.
   */
  private String author;

  /**
   * Hur mycket vi litar på att informationen är korrekt i det som källan beskriver.
   * Ett värde ej lägre än 0 (litar inte alls) och ej högre än 1 (litar fullständigt)
   */
  private Float trustworthiness;

  /**
   * exempelvis att
   * "ingen har ändrat på artikeln sedan tre månader då janne1965 städade efter ett troll"
   * eller "fann registreringsåret i en pdf som som ser ut att vara registeringsbeviset"
   */
  private String details;

  /**
   * public domain, creative commons, etc
   */
  private String license;

  /**
   * Datumet när den här informationen upptäcktes.
   *
   * exempelvis
   * datumet när janne1965 tryckte på spara-knappen,
   * eller datumet när pdf:en med registreringsbeviset skapades, tankades hem eller så.
   */
  private Date timestamp;


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Source source = (Source) o;

    if (author != null ? !author.equals(source.author) : source.author != null) return false;
    if (license != null ? !license.equals(source.license) : source.license != null) return false;
    if (details != null ? !details.equals(source.details) : source.details != null) return false;
    if (timestamp != null ? !timestamp.equals(source.timestamp) : source.timestamp != null) return false;
    if (trustworthiness != null ? !trustworthiness.equals(source.trustworthiness) : source.trustworthiness != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = author != null ? author.hashCode() : 0;
    result = 31 * result + (trustworthiness != null ? trustworthiness.hashCode() : 0);
    result = 31 * result + (details != null ? details.hashCode() : 0);
    result = 31 * result + (license != null ? license.hashCode() : 0);
    result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
    return result;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Float getTrustworthiness() {
    return trustworthiness;
  }

  public void setTrustworthiness(Float trustworthiness) {
    this.trustworthiness = trustworthiness;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
}
