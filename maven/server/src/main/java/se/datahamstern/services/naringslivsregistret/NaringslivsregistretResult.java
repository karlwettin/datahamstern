package se.datahamstern.services.naringslivsregistret;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-02-29 01:20
 *
 * @see UpdateOrganizationWithNaringslivsregistretResult
 */
public class NaringslivsregistretResult implements Serializable {

  private static final long serialVersionUID = 1l;


  private String organizationNumberPrefix;
  private String organizationNumber;
  private String organizationNumberSuffix;
  private String name;
  private String type;
  private String numericLänskod;
  private String status;

  public void setOrganizationNumberPrefix(String organizationNumberPrefix) {
    this.organizationNumberPrefix = organizationNumberPrefix;
  }

  public void setOrganizationNumber(String organizationNumber) {
    this.organizationNumber = organizationNumber;
  }

  public void setOrganizationNumberSuffix(String organizationNumberSuffix) {
    this.organizationNumberSuffix = organizationNumberSuffix;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setNumericLänskod(String numericLänskod) {
    this.numericLänskod = numericLänskod;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getOrganizationNumberPrefix() {
    return organizationNumberPrefix;
  }

  public String getOrganizationNumber() {
    return organizationNumber;
  }

  public String getOrganizationNumberSuffix() {
    return organizationNumberSuffix;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getNumericLänskod() {
    return numericLänskod;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "NaringslivsregistretResult{" +
        "organizationNumberPrefix='" + organizationNumberPrefix + '\'' +
        ", organizationNumber='" + organizationNumber + '\'' +
        ", organizationNumberSuffix='" + organizationNumberSuffix + '\'' +
        ", name='" + name + '\'' +
        ", type='" + type + '\'' +
        ", administrativeArea='" + numericLänskod + '\'' +
        ", status='" + status + '\'' +
        '}';
  }
}
