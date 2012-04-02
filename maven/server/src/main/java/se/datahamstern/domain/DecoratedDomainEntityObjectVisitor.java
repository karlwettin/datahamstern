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
public class DecoratedDomainEntityObjectVisitor implements DomainEntityObjectVisitor {

  private DomainEntityObjectVisitor decorated;

  public DecoratedDomainEntityObjectVisitor() {
  }

  public DecoratedDomainEntityObjectVisitor(DomainEntityObjectVisitor decorated) {
    this.decorated = decorated;
  }

  public DomainEntityObjectVisitor getDecorated() {
    return decorated;
  }

  public void setDecorated(DomainEntityObjectVisitor decorated) {
    this.decorated = decorated;
  }

  @Override
  public void visit(Organisation organisation) {
    getDecorated().visit(organisation);
  }

  @Override
  public void visit(Lan län) {
    getDecorated().visit(län);
  }

  @Override
  public void visit(Kommun kommun) {
    getDecorated().visit(kommun);
  }

  @Override
  public void visit(Ort ort) {
    getDecorated().visit(ort);
  }

  @Override
  public void visit(Gatuadress gatuaddress) {
    getDecorated().visit(gatuaddress);
  }

  @Override
  public void visit(Gata gata) {
    getDecorated().visit(gata);
  }

  @Override
  public void visit(Postnummer postnummer) {
    getDecorated().visit(postnummer);
  }

  @Override
  public void visit(Postort postort) {
    getDecorated().visit(postort);
  }

  @Override
  public void visit(Arsredovisning årsredovisning) {
    getDecorated().visit(årsredovisning);
  }

  @Override
  public void visit(EkonomiskPlan ekonomiskPlan) {
    getDecorated().visit(ekonomiskPlan);
  }

  @Override
  public void visit(Stadgar stadgar) {
    getDecorated().visit(stadgar);
  }

  @Override
  public void visit(Dokument dokument) {
    getDecorated().visit(dokument);
  }

  @Override
  public void visit(Dokumentversion dokumentversion) {
    getDecorated().visit(dokumentversion);
  }
}
