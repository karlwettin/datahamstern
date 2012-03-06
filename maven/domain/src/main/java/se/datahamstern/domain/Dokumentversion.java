package se.datahamstern.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import se.datahamstern.sourced.AbstractSourced;
import se.datahamstern.sourced.SourcedValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * todo Dokumentversionstext via OCR
 * todo kvalitet på versionen,
 * kan detekteras från kvalitet av OCR:ad text på
 * hurvida den får till rättstavade ord,
 * upplösning,
 * färger,
 * och naturligvis hurvida all text finns som rådata i pdf, word, etc.
 *
 * @author kalle
 * @since 2012-03-05 05:11
 */
@Entity(version = 1)
public class Dokumentversion extends AbstractSourced implements DomainEntityObject , Serializable {

  private static final long serialVersionUID = 1l;


  @Override
  public void accept(DomainObjectVisitor visitor) {
    visitor.visit(this);
  }

  @PrimaryKey
  private String identity;

  private List<SourcedValue<Dokumentversionskalla>> källor = new ArrayList<SourcedValue<Dokumentversionskalla>>();

  /** sha1 i hex som gemener, dvs 1bbcd123... */
  private String contentSha1Hex;

  private long contentLength;
  private String contentType;

  public long getContentLength() {
    return contentLength;
  }

  public void setContentLength(long contentLength) {
    this.contentLength = contentLength;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  @Override
  public String toString() {
    return "Dokumentversion{" +
        "contentType='" + contentType + '\'' +
        ", contentLength=" + contentLength +
        ", contentSha1Hex='" + contentSha1Hex + '\'' +
        ", källor=" + källor +
        ", identity='" + identity + '\'' +
        '}';
  }

  public List<SourcedValue<Dokumentversionskalla>> getKällor() {
    return källor;
  }

  public void setKällor(List<SourcedValue<Dokumentversionskalla>> källor) {
    this.källor = källor;
  }

  public String getContentSha1Hex() {
    return contentSha1Hex;
  }

  public void setContentSha1Hex(String contentSha1Hex) {
    this.contentSha1Hex = contentSha1Hex;
  }
}
