package se.datahamstern.domain;

import org.json.simple.serialization.collections.ArrayCodec;
import org.json.simple.serialization.collections.CollectionCodec;
import org.json.simple.serialization.collections.MapCodec;
import se.datahamstern.domain.hydda.*;
import se.datahamstern.domain.naringslivsregistret.Organisation;
import se.datahamstern.domain.postnummer.Gata;
import se.datahamstern.domain.postnummer.Gatuadress;
import se.datahamstern.domain.postnummer.Postnummer;
import se.datahamstern.domain.postnummer.Postort;
import se.datahamstern.domain.wikipedia.Kommun;
import se.datahamstern.domain.wikipedia.Lan;
import se.datahamstern.domain.wikipedia.Ort;
import se.datahamstern.util.ListMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

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
