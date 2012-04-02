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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author kalle
 * @since 2012-04-02 22:03
 */
public class DomainEntityObjectClasses {

  private static List<Class> classes;

  static {
    classes = new ArrayList<Class>();
    classes.add(Organisation.class);
    classes.add(Lan.class);
    classes.add(Kommun.class);
    classes.add(Ort.class);
    classes.add(Gatuadress.class);
    classes.add(Gata.class);
    classes.add(Postnummer.class);
    classes.add(Postort.class);
    classes.add(Organisation.class);
    classes.add(Stadgar.class);
    classes.add(Arsredovisning.class);
    classes.add(EkonomiskPlan.class);
    classes.add(Dokument.class);
    classes.add(Dokumentversion.class);
    classes = Collections.unmodifiableList(classes);
  }

  public static List<Class> getClasses() {
    return classes;
  }

  private class Reminder implements DomainEntityObjectVisitor {

    @Override
    public void visit(Organisation organisation) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Lan län) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Kommun kommun) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Ort ort) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Gatuadress gatuaddress) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Gata gata) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Postnummer postnummer) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Postort postort) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Arsredovisning årsredovisning) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(EkonomiskPlan ekonomiskPlan) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Stadgar stadgar) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Dokument dokument) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }

    @Override
    public void visit(Dokumentversion dokumentversion) {
      // we implement DomainEntityObjectVisitor because it reminds us at compile time to add them to the list!
    }
  }
}
