package se.datahamstern.external.kolada;

import se.datahamstern.Nop;
import se.datahamstern.io.SeleniumAccessor;

/**
 * @author kalle
 * @since 2012-04-19 12:01
 */
public class KoladaHarvester {

  // http://www.kolada.se/index.php

  public static void main(String[] args) throws Exception {
    new KoladaHarvester().harvest();
  }

  public void harvest() throws Exception {
    SeleniumAccessor selenium = new SeleniumAccessor();
    selenium.start();
    try {
      harvestDemografi(selenium);
    } finally {
      selenium.stop();
    }
  }

  public void harvestDemografi(SeleniumAccessor selenium) throws Exception {

    selenium.openAndWaitForPageToLoad("http://www.kolada.se/index.php?page=workspace/nt");
    selenium.selectFrame("workspace");
    selenium.clickAndWaitForPageToLoad("link=Start");

    selenium.clickAndWaitForPageToLoad("css=div.entry_point.search &gt; a.card &gt; div.icon");
    selenium.selectFrame("workspace");
    selenium.click("id=ext-comp-1003");
    selenium.click("css=em &gt; p");
    selenium.click("css=#ext-gen274 &gt; dt &gt; em &gt; p");
    selenium.click("css=img.flv-add-plus");
    selenium.click("xpath=(//img[@alt='Select'])[2]");
    selenium.click("xpath=(//img[@alt='Select'])[3]");
    selenium.click("xpath=(//img[@alt='Select'])[4]");
    selenium.click("xpath=(//img[@alt='Select'])[5]");
    selenium.click("xpath=(//img[@alt='Select'])[6]");
    selenium.click("xpath=(//img[@alt='Select'])[7]");
    selenium.click("xpath=(//img[@alt='Select'])[8]");
    selenium.click("xpath=(//img[@alt='Select'])[9]");
    selenium.click("xpath=(//img[@alt='Select'])[12]");
    selenium.click("id=ext-gen240");
    selenium.click("id=ext-comp-1005");
    selenium.click("css=#ext-gen351 &gt; dt &gt; em &gt; p");
    selenium.click("id=ext-gen354");
    selenium.click("id=ext-gen327");
    selenium.click("id=ext-gen81");

    Nop.breakpoint();

  }

}
