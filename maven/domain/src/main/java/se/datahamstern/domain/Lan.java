package se.datahamstern.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * @author kalle
 * @since 2012-03-02 03:22
 */
@Entity(version = 1)
public class Lan {

  @PrimaryKey
  private Long identity;


  private String name;

  @SecondaryKey(relate = Relationship.ONE_TO_ONE, name = "alphaCode")
  private String alphaCode;

  @SecondaryKey(relate = Relationship.ONE_TO_ONE, name = "numericCode")
  private String numericCode;

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlphaCode() {
    return alphaCode;
  }

  public void setAlphaCode(String alphaCode) {
    this.alphaCode = alphaCode;
  }

  public String getNumericCode() {
    return numericCode;
  }

  public void setNumericCode(String numericCode) {
    this.numericCode = numericCode;
  }
}
