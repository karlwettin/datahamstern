package se.datahamstern.external.naringslivsregistret;

/**
 * @author kalle
 * @since 2012-03-04 03:49
 */
public abstract class HarvestNaringslivsregistretVisitor {

  public void found(HarvestNaringslivsregistret harvester, NaringslivsregistretResult result)  throws Exception{

  }

  public void missing(HarvestNaringslivsregistret harvester, String organizationNumber)  throws Exception{

  }

  public void failed(HarvestNaringslivsregistret harvester, String organizationNumber, Exception e) throws Exception {

  }

  public void start(HarvestNaringslivsregistret harvester)  throws Exception{

  }

  public void end(HarvestNaringslivsregistret harvester)  throws Exception{

  }

}
