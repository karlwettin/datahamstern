package se.datahamstern.domain;

/**
 * @author kalle
 * @since 2012-03-04 04:58
 */
public interface DomainObjectVisitor {

  public abstract void visit(Organisation organisation);
  public abstract void visit(Lan län);
  public abstract void visit(Arsredovisning årsredovisning);

  public abstract void visit(EkonomiskPlan ekonomiskPlan);
  public abstract void visit(Stadgar stadgar);
  public abstract void visit(Dokument dokument);
  public abstract void visit(Dokumentversion dokumentversion);

}