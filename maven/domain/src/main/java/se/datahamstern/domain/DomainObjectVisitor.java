package se.datahamstern.domain;

import se.datahamstern.domain.hydda.*;
import se.datahamstern.domain.naringslivsregistret.Organisation;
import se.datahamstern.domain.postnummer.Gatuadress;
import se.datahamstern.domain.postnummer.Postnummer;
import se.datahamstern.domain.postnummer.Postort;
import se.datahamstern.domain.wikipedia.Kommun;
import se.datahamstern.domain.wikipedia.Lan;
import se.datahamstern.domain.wikipedia.Ort;

/**
 * @author kalle
 * @since 2012-03-04 04:58
 */
public interface DomainObjectVisitor {

  public abstract void visit(Organisation organisation);
  public abstract void visit(Lan län);
  public abstract void visit(Kommun kommun);
  public abstract void visit(Ort ort);

  public abstract void visit(Gatuadress gatuaddress);
  public abstract void visit(Postnummer postnummer);
  public abstract void visit(Postort postort);

  public abstract void visit(Arsredovisning årsredovisning);
  public abstract void visit(EkonomiskPlan ekonomiskPlan);
  public abstract void visit(Stadgar stadgar);

  public abstract void visit(Dokument dokument);
  public abstract void visit(Dokumentversion dokumentversion);

}
