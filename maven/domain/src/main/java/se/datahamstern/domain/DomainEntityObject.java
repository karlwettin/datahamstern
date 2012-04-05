package se.datahamstern.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-03-04 04:58
 */
public interface DomainEntityObject extends Serializable {

//  private static final long serialVersionUID = 1l;

  public abstract void accept(DomainEntityObjectVisitor visitor) throws Exception;
  public abstract String getIdentity();
  public abstract void setIdentity(String identity);

  /**
   * @param obj
   * @return true on equality of all values, sources, etc
   */
  @Override
  public abstract boolean equals(Object obj);

  /**
   * @return hash evaluated on all values, sources, etc.
   */
  @Override
  public abstract int hashCode();
}
