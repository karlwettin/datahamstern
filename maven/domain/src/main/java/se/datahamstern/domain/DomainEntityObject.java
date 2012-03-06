package se.datahamstern.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-03-04 04:58
 */
public interface DomainEntityObject extends Serializable {

//  private static final long serialVersionUID = 1l;


  public abstract void accept(DomainObjectVisitor visitor);
  public abstract String getIdentity();
  public abstract void setIdentity(String identity);


}
