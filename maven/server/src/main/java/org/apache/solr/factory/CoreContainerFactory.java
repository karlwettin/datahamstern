package org.apache.solr.factory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;


/**
 * @author kalle
 * @since 2011-11-12 20:24
 */
public class CoreContainerFactory {

  private CoreContainer coreContainer;

  private File path;

  private static final Logger log = LoggerFactory.getLogger(CoreContainer.class);

  public CoreContainerFactory(File solrPath) throws IOException, SAXException, ParserConfigurationException {
    this.path = solrPath;

    if (!solrPath.exists()) {
      log.warn("Creating solr root path " + solrPath.getAbsolutePath());
      if (!solrPath.mkdirs()) {
        throw new IOException("Could not mkdirs " + solrPath.getAbsolutePath());
      }
    }

    File solrHome = new File(new File(solrPath, "home").getAbsolutePath());
    if (!solrHome.exists()) {
      log.warn("Creating solr home path " + solrHome.getAbsolutePath());
      if (!solrHome.mkdirs()) {
        throw new IOException("Could not mkdirs " + solrHome.getAbsolutePath());
      }


      File solrHomeConf = new File(solrHome, "conf");
      if (!solrHomeConf.mkdirs()) {
        throw new IOException("Could not mkdirs " + solrHomeConf.getAbsolutePath());
      }

      // mandatory
      copy("/solr/home/solr.xml",new File(solrHome, "solr.xml"));

      // mandatory
      copy("/solr/home/conf/solrconfig.xml",new File(solrHomeConf, "solrconfig.xml"));
      copy(getDefaultSchema(), new File(solrHomeConf, "schema.xml"));


      // optional
      copy("/solr/home/conf/admin-extra.html",new File(solrHomeConf, "admin-extra.html"));
      copy("/solr/home/conf/elevate.xml",new File(solrHomeConf, "elevate.xml"));
      copy("/solr/home/conf/mapping-FoldToASCII.txt",new File(solrHomeConf, "mapping-FoldToASCII.txt"));
      copy("/solr/home/conf/mapping-ISOLatin1Accent.txt",new File(solrHomeConf, "mapping-ISOLAtin1Accent.txt"));
      copy("/solr/home/conf/protwords.txt",new File(solrHomeConf, "protwords.txt"));
      copy("/solr/home/conf/scripts.conf",new File(solrHomeConf, "scripts.conf"));
      copy("/solr/home/conf/spellings.txt",new File(solrHomeConf, "spellings.txt"));
      copy("/solr/home/conf/stopwords.txt",new File(solrHomeConf, "stopwords.txt"));
      copy("/solr/home/conf/stopwords_en.txt",new File(solrHomeConf, "stopwords_en.txt"));
      copy("/solr/home/conf/synonyms.txt",new File(solrHomeConf, "synonyms.txt"));


      File solrData = new File(new File(solrPath, "data").getAbsolutePath());
      if (!solrData.exists()) {
        log.warn("Creating solr data path " + solrData.getAbsolutePath());
        if (!solrData.mkdirs()) {
          throw new IOException("Could not mkdirs " + solrData.getAbsolutePath());
        }
      }

      // update data path in config file
      File solrConfigXmlFile = new File(solrHomeConf, "solrconfig.xml");
      FileUtils.writeStringToFile(solrConfigXmlFile, FileUtils.readFileToString(solrConfigXmlFile).replace("<dataDir>${solr.data.dir:PATH}</dataDir>", "<dataDir>${solr.data.dir:" + solrData.getAbsolutePath() + "}</dataDir>"));

    }

    coreContainer = new CoreContainer(solrHome.getAbsolutePath());
    coreContainer.load(solrHome.getAbsolutePath(), new File(solrHome, "solr.xml"));

  }

  private void copy(String resource, File file) throws IOException {
    copy(getClass().getResourceAsStream(resource), file);
  }

    private void copy(InputStream inputStream, File file) throws IOException {
    Reader input = new InputStreamReader(inputStream, "UTF8");
    Writer output = new OutputStreamWriter(new FileOutputStream(file));
    IOUtils.copy(input, output);
    output.close();
    input.close();
  }

  protected InputStream getDefaultSchema() {
    return getClass().getResourceAsStream("/solr/home/conf/schema.xml");
  }


  public CoreContainer getCoreContainer() {
    return coreContainer;
  }

  public File getPath() {
    return path;
  }

}
