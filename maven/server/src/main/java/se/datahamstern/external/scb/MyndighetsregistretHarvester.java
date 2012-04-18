package se.datahamstern.external.scb;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import se.datahamstern.Nop;
import se.datahamstern.io.SeleniumAccessor;
import se.datahamstern.io.SourceChangedException;

import javax.xml.xpath.XPathConstants;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kalle
 * @since 2012-04-17 13:10
 */
public class MyndighetsregistretHarvester {

  public static void main(String[] args) throws Exception {
    new MyndighetsregistretHarvester().harvest();
  }

  public void harvest() throws Exception {

    SeleniumAccessor selenium = new SeleniumAccessor();
    selenium.start();
    try {

      Set<String> linksSeen = new HashSet<String>();

      selenium.openAndWaitForPageToLoad("http://www.myndighetsregistret.scb.se/Myndighet.aspx");

      String[] options = selenium.getSelectOptions("DropDown1$ddVal");
      if (!"--- Välj myndighetsgrupp här ---".equals(options[0])) {
        throw new SourceChangedException("Expected a header in top select option!");
      }
      for (int optionIndex = 1; optionIndex < options.length; optionIndex++) {
        String option = options[optionIndex];
        selenium.selectAndWaitForPageToLoad("DropDown1$ddVal", option);
        NodeList items = (NodeList) selenium.xpath.compile("id('grd')/TBODY/TR/TD/A/@href").evaluate(selenium.getDOM(), XPathConstants.NODESET);
        if (items.getLength() == 0) {
          throw new SourceChangedException("Expected at least one item in the table");
        }
        for (int itemIndex = 0; itemIndex < items.getLength(); itemIndex++) {
          linksSeen.add(items.item(itemIndex).getTextContent());
        }
      }

      for (String link : linksSeen) {

        System.out.println(link);

        selenium.openAndWaitForPageToLoad(link);

        String namn = selenium.xpath.compile("id('txtNamn')/@value").evaluate(selenium.getDOM()).trim();
        if (namn.isEmpty()) {
          throw new SourceChangedException("Myndighetsnamn must not be empty!");
        }

        String orgno = selenium.xpath.compile("id('txtPeOrgNr')/@value").evaluate(selenium.getDOM()).trim();
        if (orgno.isEmpty()) {
          throw new SourceChangedException("Organisationsnummer must not be empty!");
        }

        String postadress = selenium.xpath.compile("id('txtPostAdr')/@value").evaluate(selenium.getDOM()).trim();
        if (postadress.isEmpty()) {
          postadress = null;
        }

        String postadressPostnummer = selenium.xpath.compile("id('txtPostNr')/@value").evaluate(selenium.getDOM()).trim();
        if (postadressPostnummer.isEmpty()) {
          throw new SourceChangedException("Postadress postnummer must not be empty!");
        }

        String postadressPostort = selenium.xpath.compile("id('txtPostOrt')/@value").evaluate(selenium.getDOM()).trim();
        if (postadressPostort.isEmpty()) {
          throw new SourceChangedException("Postadress postort must not be empty!");
        }

        String besöksadress = selenium.xpath.compile("id('txtBAdr')/@value").evaluate(selenium.getDOM()).trim();
        if (besöksadress.isEmpty()) {
          besöksadress = null;
        }
        String besöksPostnummer = selenium.xpath.compile("id('txtBPostNr')/@value").evaluate(selenium.getDOM()).trim();
        if (besöksPostnummer.isEmpty()) {
          besöksPostnummer = null;
        }
        String besöksPostort = selenium.xpath.compile("id('txtBPostOrt')/@value").evaluate(selenium.getDOM()).trim();
        if (besöksPostort.isEmpty()) {
          besöksPostort = null;
        }

        String epost = selenium.xpath.compile("id('lnkEpost')/@href").evaluate(selenium.getDOM()).replaceFirst("(mailto:)(.+)", "$2").trim();
        if (epost.isEmpty()) {
          epost = null;
        }
        String hemsida = selenium.xpath.compile("id('lnkWebbAdr')/@href").evaluate(selenium.getDOM()).trim();
        if (hemsida.isEmpty()) {
          hemsida = null;
        }
        String telefon = selenium.xpath.compile("id('txtTel')/@value").evaluate(selenium.getDOM()).trim();
        if (telefon.isEmpty()) {
          telefon = null;
        }
        String fax = selenium.xpath.compile("id('txtFax')/@value").evaluate(selenium.getDOM()).trim();
        if (fax.isEmpty()) {
          fax = null;
        }


        Nop.breakpoint();

      }

    } finally {
      selenium.stop();
    }

  }


}
