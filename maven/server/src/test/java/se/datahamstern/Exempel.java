package se.datahamstern;

import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.naringslivsregistret.Organisation;
import se.datahamstern.event.EventQueue;
import se.datahamstern.external.naringslivsregistret.CreateEventsVisitor;
import se.datahamstern.external.naringslivsregistret.HarvestNaringslivsregistret;

import java.io.File;

public class Exempel {

  public static void main(String[] args) throws Exception {

    Datahamstern.getInstance().setDataPath(new File("/tmp/datahamstern/exempel/" + System.currentTimeMillis()));
    Datahamstern.getInstance().open();
    try {

      // skörda 11 organisationer i 4 län
      new HarvestNaringslivsregistret().harvest(1, "5560743050", "5560743150", new CreateEventsVisitor());

      // uppdatera databasen med nya händelser som lagts till.
      EventQueue.getInstance().flushQueue();

      // hämta volvo personvagnar ab
      Organisation organisation = DomainStore.getInstance().getOrganisationByNummer().get("5560743089");

      // hämta strängvärdet för fältet namn i klassen Organisation.
      String namn = organisation.getNamn().get();
      if (namn != null) {
        // retunerar inte .get(); null så finns även följande metadata om namnet att tillgå:
        organisation.getNamn().getFirstSeen();
        organisation.getNamn().getLastSeen();
        organisation.getNamn().getSources();
        organisation.getNamn().getTrustworthiness();
      }

    } finally {
      Datahamstern.getInstance().close();
    }

  }

}
