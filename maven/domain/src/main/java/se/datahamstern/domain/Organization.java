package se.datahamstern.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2012-03-02 03:04
 */
@Entity(version = 1)
public class Organization {

  @PrimaryKey
  private Long identity;

  private String numberPrefix;
  @SecondaryKey(relate = Relationship.ONE_TO_ONE, name = "number")
  private String number;
  private String numberSuffix;

  private String länNumericCode;

  private String type;

  private List<OrganizationStatus> statusHistory = new ArrayList<OrganizationStatus>();

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public String getNumberPrefix() {
    return numberPrefix;
  }

  public void setNumberPrefix(String numberPrefix) {
    this.numberPrefix = numberPrefix;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getNumberSuffix() {
    return numberSuffix;
  }

  public void setNumberSuffix(String numberSuffix) {
    this.numberSuffix = numberSuffix;
  }

  public String getLänNumericCode() {
    return länNumericCode;
  }

  public void setLänNumericCode(String länNumericCode) {
    this.länNumericCode = länNumericCode;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<OrganizationStatus> getStatusHistory() {
    return statusHistory;
  }

  public void setStatusHistory(List<OrganizationStatus> statusHistory) {
    this.statusHistory = statusHistory;
  }
}
