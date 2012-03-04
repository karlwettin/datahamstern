package se.datahamstern.domain;

/**
 * @author kalle
 * @since 2012-03-04 04:58
 */
public interface DomainEntityObject {

  public abstract void accept(DomainObjectVisitor visitor);
  public abstract String getIdentity();
  public abstract void setIdentity(String identity);


}
