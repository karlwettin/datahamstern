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
 * @since 2012-04-02 21:52
 */
public class AllDomainEntityObjectVisitor implements DomainEntityObjectVisitor {

  public void visit(DomainEntityObject object) {
      
  }
  
  @Override
  public void visit(Organisation organisation) {
    visit((DomainEntityObject)organisation);
  }

  @Override
  public void visit(Lan län) {
    visit((DomainEntityObject)län);
  }

  @Override
  public void visit(Kommun kommun) {
    visit((DomainEntityObject)kommun);
  }

  @Override
  public void visit(Ort ort) {
    visit((DomainEntityObject)ort);
  }

  @Override
  public void visit(Gatuadress gatuaddress) {
    visit((DomainEntityObject)gatuaddress);
  }

  @Override
  public void visit(Gata gata) {
    visit((DomainEntityObject)gata);
  }

  @Override
  public void visit(Postnummer postnummer) {
    visit((DomainEntityObject)postnummer);
  }

  @Override
  public void visit(Postort postort) {
    visit((DomainEntityObject)postort);
  }

  @Override
  public void visit(Arsredovisning årsredovisning) {
    visit((DomainEntityObject)årsredovisning);
  }

  @Override
  public void visit(EkonomiskPlan ekonomiskPlan) {
    visit((DomainEntityObject)ekonomiskPlan);
  }

  @Override
  public void visit(Stadgar stadgar) {
    visit((DomainEntityObject)stadgar);
  }

  @Override
  public void visit(Dokument dokument) {
    visit((DomainEntityObject)dokument);
  }

  @Override
  public void visit(Dokumentversion dokumentversion) {
    visit((DomainEntityObject)dokumentversion);
  }
}
