package se.datahamstern.services.naringslivsregistret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import se.datahamstern.io.SeleniumAccessor;
import se.datahamstern.io.SourceChangedException;
import se.datahamstern.util.Mod10;
import se.datahamstern.xml.DomUtils;

import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2012-01-13 18:11
 */
public class Naringslivsregistret {

//  private static final Logger log = LoggerFactory.getLogger(Naringslivsregistret.class);

  private SeleniumAccessor selenium;

  public void open() {
    selenium = new SeleniumAccessor();
    selenium.start();
    selenium.openAndWaitForPageToLoad("http://www.bolagsverket.se/");
    selenium.clickAndWaitForPageToLoad("//A[text()='Näringslivsregistret']");
  }

  public void close() {
    selenium.stop();
  }

  // todo milliseconds delay?

  public List<NaringslivsregistretResult> search(String query) throws Exception {

    List<NaringslivsregistretResult> results = new ArrayList<NaringslivsregistretResult>();

    selenium.getEval("window.document.getElementById('sokstrang').value='" + query + "';");
    selenium.select("//SELECT[@id='sokalternativ']", "Företag");
    selenium.clickAndWaitForPageToLoad("//FORM[@name='sokForm']//INPUT[@type='submit']");

    while (true) {

      NodeList resultNodes = (NodeList) selenium.xpath.compile("//TABLE[@class='bv-wide']/TBODY/TR").evaluate(selenium.getDOM(), XPathConstants.NODESET);
      for (int i = 0; i < resultNodes.getLength(); i++) {
        Node resultNode = resultNodes.item(i);

        NaringslivsregistretResult result = new NaringslivsregistretResult();

        String organizationNumber = DomUtils.replaceNonBreakingSpaces(selenium.xpath.compile("TD[@headers='h-orgnr']").evaluate(resultNode)).replaceAll("\\s+", " ").trim();
        Matcher matcher = Pattern.compile("(\\d+ )?(\\d{6})-(\\d{4})( \\d+)?").matcher(organizationNumber);
        if (!matcher.find()) {
          throw new SourceChangedException();
        }
        if (matcher.group(1) != null) {
          result.setOrganizationNumberPrefix(matcher.group(1).trim());
        }

        result.setOrganizationNumber(matcher.group(2).trim() + matcher.group(3).trim());

        if (matcher.group(4) != null) {
          result.setOrganizationNumberSuffix(matcher.group(4).trim());
        }

        result.setName(DomUtils.replaceNonBreakingSpaces(selenium.xpath.compile("TD[@headers='h-firma']").evaluate(resultNode)).replaceAll("\\s+", " ").trim());
        result.setType(DomUtils.replaceNonBreakingSpaces(selenium.xpath.compile("TD[@headers='h-form']").evaluate(resultNode)).replaceAll("\\s+", " ").trim());
        result.setNumericLänskod(DomUtils.replaceNonBreakingSpaces(selenium.xpath.compile("TD[@headers='h-lan']").evaluate(resultNode)).replaceAll("\\s+", " ").trim());
        result.setStatus(DomUtils.replaceNonBreakingSpaces(selenium.xpath.compile("TD[@headers='h-status']").evaluate(resultNode)).replaceAll("\\s+", " ").trim());
        if (result.getStatus().isEmpty()) {
          result.setStatus(null);
        }

        results.add(result);
      }

      if (!selenium.isElementPresent("//A[text()='Nästa']")) {
        break;
      }
      selenium.clickAndWaitForPageToLoad("//A[text()='Nästa']");

    }


    // if query is organization number then we don't get the county code
    // so place a secondary query using the name and then find the result in the list using the organization number

    if (Mod10.isValidSwedishOrganizationNumber(query.replaceAll("\\p{Punct}", ""))) {
      for (NaringslivsregistretResult result : results) {
        List<NaringslivsregistretResult> nameResults = search(result.getName());
        for (NaringslivsregistretResult nameResult : nameResults) {
          if (nameResult.getOrganizationNumber().equals(result.getOrganizationNumber())) {
            result.setNumericLänskod(nameResult.getNumericLänskod());
            break;
          }
        }
      }
    }

    return results;
  }

}
