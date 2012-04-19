package se.datahamstern.domain;

import se.datahamstern.domain.hydda.*;

/**
 * @author kalle
 * @since 2012-04-02 21:52
 */
public class AllDomainEntityObjectVisitor implements DomainEntityObjectVisitor {


  public <E extends DomainEntityObject> void visit(Class<E> type, E object) throws Exception {

  }


  @Override
  public void visit(Organisation organisation) throws Exception {
    visit(Organisation.class, organisation);
  }

  @Override
  public void visit(Lan län) throws Exception {
    visit(Lan.class, län);
  }

  @Override
  public void visit(Kommun kommun) throws Exception {
    visit(Kommun.class, kommun);
  }

  @Override
  public void visit(Ort ort) throws Exception {
    visit(Ort.class, ort);
  }

  @Override
  public void visit(Gatuadress gatuaddress) throws Exception {
    visit(Gatuadress.class, gatuaddress);
  }

  @Override
  public void visit(Gata gata) throws Exception {
    visit(Gata.class, gata);
  }

  @Override
  public void visit(Postnummer postnummer) throws Exception {
    visit(Postnummer.class, postnummer);
  }

  @Override
  public void visit(Postort postort) throws Exception {
    visit(Postort.class, postort);
  }

  @Override
  public void visit(Arsredovisning årsredovisning) throws Exception {
    visit(Arsredovisning.class, årsredovisning);
  }

  @Override
  public void visit(EkonomiskPlan ekonomiskPlan) throws Exception {
    visit(EkonomiskPlan.class, ekonomiskPlan);
  }

  @Override
  public void visit(Stadgar stadgar) throws Exception {
    visit(Stadgar.class, stadgar);
  }

  @Override
  public void visit(Dokument dokument) throws Exception {
    visit(Dokument.class, dokument);
  }

  @Override
  public void visit(Dokumentversion dokumentversion) throws Exception {
    visit(Dokumentversion.class, dokumentversion);
  }
}
