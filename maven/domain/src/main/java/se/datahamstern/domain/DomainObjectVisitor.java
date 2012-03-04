package se.datahamstern.domain;

/**
 * @author kalle
 * @since 2012-03-04 04:58
 */
public interface DomainObjectVisitor {

  public abstract void visit(Organisation organisation);
  public abstract void visit(Lan län);

}
