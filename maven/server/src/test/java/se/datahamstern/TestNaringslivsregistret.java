package se.datahamstern;

import junit.framework.TestCase;
import org.json.simple.parser.JSONParser;
import se.datahamstern.domain.DomainStore;
import se.datahamstern.domain.Lan;
import se.datahamstern.domain.Organisation;
import se.datahamstern.event.EventExecutor;
import se.datahamstern.external.naringslivsregistret.Naringslivsregistret;
import se.datahamstern.external.naringslivsregistret.NaringslivsregistretCommand;
import se.datahamstern.external.naringslivsregistret.NaringslivsregistretResult;
import se.datahamstern.sourced.SourcedValue;
import se.datahamstern.util.Mod10;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author kalle
 * @since 2012-03-04 03:43
 */
public class TestNaringslivsregistret extends TestCase {

  public void test() throws Exception {

    Datahamstern.getInstance().setDataPath(new File("/tmp/datahamstern/data/" + System.currentTimeMillis()));
    Datahamstern.getInstance().open();
    try {

      final Date started = new Date();

      Naringslivsregistret nlr = new Naringslivsregistret();
      nlr.open();
      try {

        JSONParser jsonParser = new JSONParser();
        List<NaringslivsregistretResult> results = new ArrayList<NaringslivsregistretResult>(nlr.search("volvo personvagnar").keySet());
        for (NaringslivsregistretResult result : results) {
          EventExecutor.getInstance().execute(NaringslivsregistretCommand.eventFactory(result), jsonParser);
        }

        // todo assert timestamps in sources are in between started and finished.


        Lan län = DomainStore.getInstance().getLänByNummerkod().get("14");
        assertNotNull(län);
        assertNotNull(län.getNummerkod());

        Map<String, Organisation> organisationer = DomainStore.getInstance().getOrganisationerByLän().map();
        assertNotNull(organisationer);
        assertFalse(organisationer.isEmpty());
        assertTrue(organisationer.size() == results.size());


        boolean hasStatus = false;
        boolean hasNummerprefix = false;
        boolean hasNummersuffix = false;


        for (Organisation organisation : organisationer.values()) {

          if (organisation.getNummer().get().equals("5560743089")) {
            assertNotNull(organisation);
            assertNotNull(organisation.getLänIdentity().get());
            assertEquals("Volvo Personvagnar Aktiebolag", organisation.getNamn().get());
            assertEquals(null, organisation.getNummerprefix().get());
            assertEquals("5560743089", organisation.getNummer().get());
            assertEquals(null, organisation.getNummersuffix().get());
            assertEquals("AB", organisation.getTyp().get());
            assertTrue(organisation.getStatus() == null || organisation.getStatus().isEmpty());
          }

          assertNotNull(organisation);
          assertNotNull(organisation.getLänIdentity().get());
          assertNotNull(organisation.getTyp().get());

          /**
           * todo finns tillgängligt från näringslivsregistret!
           *
           * todo osäker på hur detta hänger ihop, är det samma organisationsnummer med olika namn?
           * todo lagras för närvarande inte i bdb.
           *
           *
           * https://snr4.bolagsverket.se/snrgate/hjalp.do?method=hjalp.firmatyper
           * Näringslivsregistret - Firmatyper
           * Följande firmatyper kan visas:
           * Firma: Företagsnamn
           * Bifirma: Ett extra företagsnamn för en del av företagets verksamhet
           * Parallellfirma: Företagets namn (firma) översatt till annat språk
           */
//          assertNotNull(organisation.getFirmatyp().get());

          assertNotNull(organisation.getNummer().get());
          assertTrue(Mod10.isValidSwedishOrganizationNumber(organisation.getNummer().get()));
          if (organisation.getNummerprefix().get() != null) hasNummerprefix = true;
          if (organisation.getNummersuffix().get() != null) hasNummersuffix = true;

          if (organisation.getStatus() != null && !organisation.getStatus().isEmpty()) {
            for (SourcedValue<String> status : organisation.getStatus()) {
              if (status.get() != null && !status.get().trim().isEmpty()) {
                hasStatus = true;
              }
            }
          }

        }

        assertTrue(hasStatus);

        // todo hitta och testa exempel som har det!!

        assertFalse(hasNummerprefix);
        assertFalse(hasNummersuffix);

      } finally {
        nlr.close();
      }
    } finally {
      Datahamstern.getInstance().close();
    }
  }

}
