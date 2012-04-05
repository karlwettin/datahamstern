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
 * @since 2012-03-04 04:58
 */
public interface DomainEntityObjectVisitor<Exception extends java.lang.Exception> {

  public abstract void visit(Organisation organisation) throws Exception;
  public abstract void visit(Lan län) throws Exception;
  public abstract void visit(Kommun kommun) throws Exception;
  public abstract void visit(Ort ort) throws Exception;

  public abstract void visit(Gatuadress gatuaddress) throws Exception;
  public abstract void visit(Gata gata) throws Exception;
  public abstract void visit(Postnummer postnummer) throws Exception;
  public abstract void visit(Postort postort) throws Exception;

  public abstract void visit(Arsredovisning årsredovisning) throws Exception;
  public abstract void visit(EkonomiskPlan ekonomiskPlan) throws Exception;
  public abstract void visit(Stadgar stadgar) throws Exception;

  public abstract void visit(Dokument dokument) throws Exception;
  public abstract void visit(Dokumentversion dokumentversion) throws Exception;

}
