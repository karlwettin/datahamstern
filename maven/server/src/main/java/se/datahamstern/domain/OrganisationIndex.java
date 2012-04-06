package se.datahamstern.domain;

import com.sleepycat.persist.EntityCursor;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.factory.CoreContainerFactory;
import se.datahamstern.domain.naringslivsregistret.Organisation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author kalle
 * @since 2012-04-05 22:30
 */
public class OrganisationIndex {

  private static final OrganisationIndex instance = new OrganisationIndex();

  private OrganisationIndex() {
  }

  public static OrganisationIndex getInstance() {
    return instance;
  }

  private SolrServer solrServer;

  private CoreContainer coreContainer;

  private File path;

  public void open() throws Exception {
    if (coreContainer == null) {
      coreContainer = new CoreContainerFactory(path) {
        @Override
        protected InputStream getDefaultSchema() {
          return getClass().getResourceAsStream("/se/datahamstern/domain/OrganisationIndex.schema.xml");
        }
      }.getCoreContainer();
    }
    if (solrServer == null) {
      solrServer = new EmbeddedSolrServer(coreContainer, "");
      System.out.println("solr server started");
    }

    if (solrServer.query(new SolrQuery("*:*").setRows(1)).getResults().getNumFound() == 0) {
      // todo construct index
    }
  }

  public void close() {
    solrServer = null;
    CoreContainer coreContainer = this.coreContainer;
    this.coreContainer = null;
    coreContainer.shutdown();
  }

  public File getPath() {
    return path;
  }

  public void setPath(File path) {
    this.path = path;
  }


  public QueryResponse search(String query) throws Exception {

    SolrQuery solrQuery = new SolrQuery(query);
    solrQuery.setRows(100);
    solrQuery.setStart(0);
    solrQuery.set("defType", "edismax");

//    solrQuery.set("mm", "100%"); // the effect of this is AND operator
    solrQuery.set("mm", "50%"); // the effect of this, i dont know.

    StringBuilder qf = new StringBuilder();
    qf.append("nummer^1000 ");
    qf.append("namn^100 ");
    qf.append("namn_without_diacritics^50 ");
    qf.append("namn_front_ngrams^5 ");
    qf.append("namn_ngrams^1 ");

    solrQuery.set("qf", qf.toString());
    solrQuery.addField("score");
    solrQuery.addField("identity");
    QueryResponse queryResponse;
    try {
      queryResponse = solrServer.query(solrQuery);
    } catch (NullPointerException e) {
      throw e;
    }
    return queryResponse;


  }


  public void reconstruct() throws Exception {

    Organisation organisation;
    EntityCursor<Organisation> cursor = DomainStore.getInstance().getOrganisationer().entities();
    try {
      while ((organisation = cursor.next()) != null) {
        put(organisation);
      }
    } finally {
      cursor.close();
    }


    solrServer.commit();

  }

  public void put(Organisation organisation) throws SolrServerException, IOException {
    SolrInputDocument document = new SolrInputDocument();

    document.addField("identity", organisation.getIdentity());
    document.addField("nummer", organisation.getNummer());
    document.addField("namn", organisation.getNamn());
    document.addField("namn_without_diacritics", organisation.getNamn());
    document.addField("namn_ngrams", organisation.getNamn());
    document.addField("namn_front_ngrams", organisation.getNamn());

    solrServer.add(document);
  }

  public SolrServer getSolrServer() {
    return solrServer;
  }

  public void setSolrServer(SolrServer solrServer) {
    this.solrServer = solrServer;
  }
}
