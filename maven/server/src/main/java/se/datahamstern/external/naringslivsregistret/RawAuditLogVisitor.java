package se.datahamstern.external.naringslivsregistret;

import se.datahamstern.Datahamstern;

import java.io.*;
import java.util.Date;

/**
 * this is not the same as the one producing HarvestNaringslivsregistret.raw... etc
 *
 * @author kalle
 * @since 2012-03-04 03:56
 */
public class RawAuditLogVisitor extends HarvestNaringslivsregistretVisitor {

  public static int FAILED_POST = -3;
  public static int EOF_POST = -2;
  public static int BOF_POST = -1; 
      
  public static int MISSING_POST = 0; 
  public static int FOUND_POST = 1; 
  
  private File file;

  public RawAuditLogVisitor() {
  }

  public RawAuditLogVisitor(File file) {
    this.file = file;
  }

  private ObjectOutputStream out;
  
  @Override
  public void start(HarvestNaringslivsregistret harvester)  throws Exception{

    if (file == null) {
      file = new File(Datahamstern.getInstance().getDataPath(), "services/bolagsverket/naringslivsregistret/RawAuditLogVisitor");
      if (!file.exists()  && !file.mkdirs()) {
        throw new IOException("Could not mkdirs " + file.getAbsolutePath());
      }
      file = new File(file, HarvestNaringslivsregistret.toString(harvester.getOrganizationNumber())+ "-" + System.currentTimeMillis() + ".bin");
    }

    out = new ObjectOutputStream(new FileOutputStream(file));
    out.writeInt(BOF_POST);     
  }

  @Override
  public void missing(HarvestNaringslivsregistret harvester, String organizationNumber)  throws Exception{
    out.writeInt(MISSING_POST);
    out.writeObject(organizationNumber);
  }

  @Override
  public void found(HarvestNaringslivsregistret harvester, NaringslivsregistretResult result)  throws Exception{
    out.writeInt(FOUND_POST);
    
    out.writeObject(new Date());
    out.writeObject(result.getNummerprefix());
    out.writeObject(result.getNummer());
    out.writeObject(result.getNummersuffix());
    out.writeObject(result.getLänsnummer());
    out.writeObject(result.getFirmaform());
    out.writeObject(result.getNamn());
    out.writeObject(result.getStatus());
               
    out.flush();
  }

  @Override
  public void failed(HarvestNaringslivsregistret harvester, String organizationNumber, Exception e) throws Exception {
    out.writeInt(FAILED_POST);
    out.writeObject(organizationNumber);
    out.writeObject(e);
    out.flush();
  }

  @Override
  public void end(HarvestNaringslivsregistret harvester)  throws Exception{
    out.close();
  }
}
