package se.datahamstern.domain;

import com.sleepycat.persist.model.Persistent;

import java.util.Date;

/**
 * @author kalle
 * @since 2012-03-02 03:37
 */
@Persistent (version = 1)
public class OrganizationStatus {

  private Date lastSeen = new Date();

  private Date firstSeen = lastSeen;

  private Date created = lastSeen;
  private boolean certifiedCreated = false;

  private String text;


  public Date getLastSeen() {
    return lastSeen;
  }

  public void setLastSeen(Date lastSeen) {
    this.lastSeen = lastSeen;
  }

  public Date getFirstSeen() {
    return firstSeen;
  }

  public void setFirstSeen(Date firstSeen) {
    this.firstSeen = firstSeen;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public boolean isCertifiedCreated() {
    return certifiedCreated;
  }

  public void setCertifiedCreated(boolean certifiedCreated) {
    this.certifiedCreated = certifiedCreated;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
