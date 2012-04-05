package se.datahamstern.domain;

import se.datahamstern.domain.hydda.*;
import se.datahamstern.domain.naringslivsregistret.Organisation;
import se.datahamstern.domain.postnummer.Gata;
import se.datahamstern.domain.postnummer.Gatuadress;
import se.datahamstern.domain.postnummer.Postnummer;
import se.datahamstern.domain.postnummer.Postort;
import se.datahamstern.domain.wikipedia.Kommun;
import se.datahamstern.domain.wikipedia.Lan;
import se.datahamstern.domain.wikipedia.Ort;

/**
 * @author kalle
 * @since 2012-04-02 21:54
 */
public class DecoratedDomainEntityObjectVisitor<E extends Exception> implements DomainEntityObjectVisitor<E> {

  private DomainEntityObjectVisitor<E> decorated;

  public DecoratedDomainEntityObjectVisitor() {
  }

  public DecoratedDomainEntityObjectVisitor(DomainEntityObjectVisitor<E> decorated) {
    this.decorated = decorated;
  }

  public DomainEntityObjectVisitor<E> getDecorated() {
    return decorated;
  }

  public void setDecorated(DomainEntityObjectVisitor<E> decorated) {
    this.decorated = decorated;
  }

  @Override
  public void visit(Organisation organisation) throws E {
    getDecorated().visit(organisation);
  }

  @Override
  public void visit(Lan län) throws E  {
    getDecorated().visit(län);
  }

  @Override
  public void visit(Kommun kommun) throws E  {
    getDecorated().visit(kommun);
  }

  @Override
  public void visit(Ort ort)  throws E {
    getDecorated().visit(ort);
  }

  @Override
  public void visit(Gatuadress gatuaddress)  throws E {
    getDecorated().visit(gatuaddress);
  }

  @Override
  public void visit(Gata gata)  throws E {
    getDecorated().visit(gata);
  }

  @Override
  public void visit(Postnummer postnummer)  throws E {
    getDecorated().visit(postnummer);
  }

  @Override
  public void visit(Postort postort)  throws E {
    getDecorated().visit(postort);
  }

  @Override
  public void visit(Arsredovisning årsredovisning) throws E  {
    getDecorated().visit(årsredovisning);
  }

  @Override
  public void visit(EkonomiskPlan ekonomiskPlan) throws E  {
    getDecorated().visit(ekonomiskPlan);
  }

  @Override
  public void visit(Stadgar stadgar) throws E  {
    getDecorated().visit(stadgar);
  }

  @Override
  public void visit(Dokument dokument) throws E  {
    getDecorated().visit(dokument);
  }

  @Override
  public void visit(Dokumentversion dokumentversion)  throws E {
    getDecorated().visit(dokumentversion);
  }
}
