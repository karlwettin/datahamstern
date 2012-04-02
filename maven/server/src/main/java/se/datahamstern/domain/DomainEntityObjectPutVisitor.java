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
import se.datahamstern.sourced.SourcedValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author kalle
 * @since 2012-04-02 21:33
 */
public class DomainEntityObjectPutVisitor implements DomainEntityObjectVisitor {
  @Override
  public void visit(Lan län) {
    DomainStore.getInstance().assignIdentity(län);
    län.set_index_alfakod(län.getAlfakod().get());
    län.set_index_nummerkod(län.getNummerkod().get());
    län.set_index_namn(län.getNamn().get());
    DomainStore.getInstance().getLän().put(län);
  }

  @Override
  public void visit(Kommun kommun) {
    DomainStore.getInstance().assignIdentity(kommun);
    kommun.set_index_länIdentity(kommun.getLänIdentity().get());
    kommun.set_index_namn(kommun.getNamn().get());
    DomainStore.getInstance().getKommuner().put(kommun);
  }

  @Override
  public void visit(Ort ort) {
    DomainStore.getInstance().assignIdentity(ort);
    ort.set_index_kommunIdentity(ort.getKommunIdentity().get());
    DomainStore.getInstance().getOrter().put(ort);
  }

  @Override
  public void visit(Organisation organisation) {
    DomainStore.getInstance().assignIdentity(organisation);
    organisation.set_index_nummer(organisation.getNummer().get());
    organisation.set_index_länIdentity(organisation.getLänIdentity().get());
    DomainStore.getInstance().getOrganisationer().put(organisation);
  }

  @Override
  public void visit(Arsredovisning årsredovisning) {
    DomainStore.getInstance().assignIdentity(årsredovisning);
    årsredovisning.set_index_organisationIdentity(årsredovisning.getOrganisationIdentity().get());
    if (årsredovisning.getDatumFrom().get() != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(årsredovisning.getDatumFrom().get());
      årsredovisning.set_index_år(calendar.get(Calendar.YEAR));
    } else {
      årsredovisning.set_index_år(null);
    }
    DomainStore.getInstance().getÅrsredovisningar().put(årsredovisning);

  }

  @Override
  public void visit(EkonomiskPlan ekonomiskPlan) {
    DomainStore.getInstance().assignIdentity(ekonomiskPlan);
    ekonomiskPlan.set_index_organisationIdentity(ekonomiskPlan.getOrganisationIdentity().get());
    DomainStore.getInstance().getEkonomiskaPlaner().put(ekonomiskPlan);

  }

  @Override
  public void visit(Stadgar stadgar) {
    DomainStore.getInstance().assignIdentity(stadgar);
    stadgar.set_index_organisationIdentity(stadgar.getOrganisationIdentity().get());
    DomainStore.getInstance().getStadgar().put(stadgar);
  }

  @Override
  public void visit(Dokument dokument) {
    DomainStore.getInstance().assignIdentity(dokument);
    if (dokument.getDokumentversionerIdentity() != null && !dokument.getDokumentversionerIdentity().isEmpty()) {
      dokument.set_index_dokumentversionerIdentity(new ArrayList<String>(dokument.getDokumentversionerIdentity().size()));
      for (SourcedValue<String> dokumentVersionIdentity : dokument.getDokumentversionerIdentity()) {
        dokument.get_index_dokumentversionerIdentity().add(dokumentVersionIdentity.get());
      }
    }
    DomainStore.getInstance().getDokument().put(dokument);

  }

  @Override
  public void visit(Dokumentversion dokumentversion) {
    DomainStore.getInstance().assignIdentity(dokumentversion);
    DomainStore.getInstance().getDokumentversioner().put(dokumentversion);
  }

  @Override
  public void visit(Gatuadress gatuaddress) {
    DomainStore.getInstance().assignIdentity(gatuaddress);
    gatuaddress.set_index_postnummerIdentity(gatuaddress.getPostnummerIdentity().get());
    gatuaddress.set_index_gataIdentity(gatuaddress.getGataIdentity().get());
    Gatuadress.GataAndGatunummer uniqueIndex = gatuaddress.get_index_gataAndGatunummer();
    if (uniqueIndex == null) {
      gatuaddress.set_index_gataAndGatunummer(uniqueIndex = new Gatuadress.GataAndGatunummer());
    }
    uniqueIndex.setGataIdentity(gatuaddress.getGataIdentity().get());
    uniqueIndex.setGatunummer(gatuaddress.getGatunummer().get());

    DomainStore.getInstance().getGatuadresser().put(gatuaddress);
  }

  @Override
  public void visit(Gata gata) {
    DomainStore.getInstance().assignIdentity(gata);
    gata.set_index_postortIdentity(gata.getPostortIdentity().get());
    List<String> index = new ArrayList<String>(gata.getPostnummerIdentities().size());
    for (SourcedValue<String> identity : gata.getPostnummerIdentities()) {
      index.add(identity.get());
    }
    gata.set_index_postnummerIdentities(index);

    if (gata.get_index_namnAndPostort() == null) {
      gata.set_index_namnAndPostort(new Gata.NamnAndPostort());
    }
    gata.get_index_namnAndPostort().setGatunamn(gata.getNamn().get());
    gata.get_index_namnAndPostort().setPostortIdentity(gata.getPostortIdentity().get());

    DomainStore.getInstance().getGator().put(gata);
  }

  @Override
  public void visit(Postnummer postnummer) {
    DomainStore.getInstance().assignIdentity(postnummer);
    postnummer.set_index_postnummer(postnummer.getPostnummer().get());
    postnummer.set_index_postortIdentity(postnummer.getPostortIdentity().get());
    DomainStore.getInstance().getPostnummer().put(postnummer);
  }

  @Override
  public void visit(Postort postort) {
    DomainStore.getInstance().assignIdentity(postort);
    postort.set_index_namn(postort.getNamn().get());
    DomainStore.getInstance().getPostorter().put(postort);
  }
  
}
