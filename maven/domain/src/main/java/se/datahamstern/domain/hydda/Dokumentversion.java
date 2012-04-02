package se.datahamstern.domain.hydda;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import se.datahamstern.domain.DomainEntityObject;
import se.datahamstern.domain.DomainEntityObjectVisitor;
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
public class Dokumentversion extends AbstractSourced implements DomainEntityObject, Serializable {

  private static final long serialVersionUID = 1l;


  @Override
  public void accept(DomainEntityObjectVisitor visitor) {
    visitor.visit(this);
  }

  @PrimaryKey
  private String identity;

  private List<SourcedValue<Dokumentversionskalla>> källor = new ArrayList<SourcedValue<Dokumentversionskalla>>();

  /** sha1 i hex som gemener, dvs 1bbcd123... */
  private String contentSha1Hex;

  private long contentLength;
  private String contentType;

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Dokumentversion that = (Dokumentversion) o;

    if (contentLength != that.contentLength) return false;
    if (contentSha1Hex != null ? !contentSha1Hex.equals(that.contentSha1Hex) : that.contentSha1Hex != null) return false;
    if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;
    if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;
    if (källor != null ? !källor.equals(that.källor) : that.källor != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (identity != null ? identity.hashCode() : 0);
    result = 31 * result + (källor != null ? källor.hashCode() : 0);
    result = 31 * result + (contentSha1Hex != null ? contentSha1Hex.hashCode() : 0);
    result = 31 * result + (int) (contentLength ^ (contentLength >>> 32));
    result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
    return result;
  }

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
