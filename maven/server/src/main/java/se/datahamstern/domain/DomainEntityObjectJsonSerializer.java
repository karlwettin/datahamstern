package se.datahamstern.domain;

import org.json.simple.JSONObject;
import se.datahamstern.domain.hydda.*;
import se.datahamstern.domain.naringslivsregistret.Organisation;
import se.datahamstern.domain.postnummer.Gata;
import se.datahamstern.domain.postnummer.Gatuadress;
import se.datahamstern.domain.postnummer.Postnummer;
import se.datahamstern.domain.postnummer.Postort;
import se.datahamstern.domain.wikipedia.Kommun;
import se.datahamstern.domain.wikipedia.Lan;
import se.datahamstern.domain.wikipedia.Ort;
import se.datahamstern.sourced.SourcedInterface;
import se.datahamstern.sourced.SourcedValue;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;

/**
 * marshalls domain ojects to json
 *
 * could be implemented with reflection at the cost of cpu.
 * todo lean back on reflection for non implemented visiting methods?
 *
 * @author kalle
 * @since 2012-04-05 19:06
 */
public class DomainEntityObjectJsonSerializer implements DomainEntityObjectVisitor<IOException> {

  public DomainEntityObjectJsonSerializer() {
  }

  private Writer writer;
  private boolean writingMetadata;

  public DomainEntityObjectJsonSerializer(Writer writer, boolean writingMetadata) {
    this.writer = writer;
    this.writingMetadata = writingMetadata;
  }

  public Writer getWriter() {
    return writer;
  }

  public void setWriter(Writer writer) {
    this.writer = writer;
  }

  public boolean isWritingMetadata() {
    return writingMetadata;
  }

  public void setWritingMetadata(boolean writingMetadata) {
    this.writingMetadata = writingMetadata;
  }

  private void writeSourcedKeyValueString(boolean commaPrefix, String key, SourcedValue<String> value) throws IOException {
    if (commaPrefix) {
      writer.write(',');
    }
    writeKey(key);
    if (value.get() == null) {
      writer.write("null");
    } else {
      if (!writingMetadata) {
        writeValue(value.get());
      } else {
        writer.write("{");
        writeKey("value");

        writeValue(value.get());

        writer.write(',');
        writeSourced(value);

        writer.write("}");
      }
    }
  }

  private void writeSourcedKeyValueInteger(boolean commaPrefix, String key, SourcedValue<Integer> value) throws IOException {
    if (commaPrefix) {
      writer.write(',');
    }
    writeKey(key);
    if (value.get() == null) {
      writer.write("null");
    } else {
      if (!writingMetadata) {
        writeValue(value.get());
      } else {
        writer.write("{");
        writeKey("value");
        writeValue(value.get());

        writer.write(',');
        writeSourced(value);

        writer.write("}");
      }
    }
  }

  private void writeSourced(SourcedInterface value) throws IOException {
    writeKey("lastSeen");
    writeValue(value.getLastSeen());

    writer.write(',');
    writeKey("firstSeen");
    writeValue(value.getFirstSeen());

    writer.write(',');
    writeKey("trustworthiness");
    writeValue(value.getTrustworthiness());

    writer.write(',');
    writeKey("sources");
    if (value.getSources() == null) {
      writer.write("null");
    } else {
      writer.write("{");
      writeKey("authors");
      if (value.getSources().getAuthors() == null || value.getSources().getAuthors().isEmpty()) {
        writer.write("null");
      } else {
        writer.write('[');
        for (Iterator<String> iterator = value.getSources().getAuthors().iterator(); iterator.hasNext(); ) {
          String author = iterator.next();
          writeValue(author);
          if (iterator.hasNext()) {
            writer.write(',');
          }
        }
        writer.write(']');
      }

      writer.write(',');
      writeKey("licenses");
      if (value.getSources().getLicenses() == null || value.getSources().getLicenses().isEmpty()) {
        writer.write("null");
      } else {
        writer.write('[');
        for (Iterator<String> iterator = value.getSources().getLicenses().iterator(); iterator.hasNext(); ) {
          String license = iterator.next();
          writeValue(license);
          if (iterator.hasNext()) {
            writer.write(',');
          }
        }
        writer.write(']');
      }


      writer.write("}");
    }
  }

  private void writeSourcedValueString(boolean commaPrefix, SourcedValue<String> value) throws IOException {
    if (commaPrefix) {
      writer.write(',');
    }
    if (value.get() == null) {
      writer.write("null");
    } else {
      if (writingMetadata) {
        writer.write("{");
        writeKey("value");
        writeValue(value.get());
        writer.write(',');
        writeSourced(value);
        writer.write("}");
      } else {
        writeValue(value.get());
      }
    }
  }

  private void writeIdentity(DomainEntityObject object) throws IOException {
    writeKey("identity");
    writeValue(object.getIdentity());
  }

  private void writeValue(String value) throws IOException {
    if (value == null) {
      writer.write("null");
    } else {
      writer.write('"');
      writer.write(JSONObject.escape(value));
      writer.write('"');
    }
  }

  private void writeValue(Date value) throws IOException {
    if (value == null) {
      writer.write("null");
    } else {
      writer.write(String.valueOf(value.getTime()));
    }
  }

  private void writeValue(Integer value) throws IOException {
    if (value == null) {
      writer.write("null");
    } else {
      writer.write(String.valueOf(value));
    }
  }

  private void writeValue(Long value) throws IOException {
    if (value == null) {
      writer.write("null");
    } else {
      writer.write(String.valueOf(value));
    }
  }

  private void writeValue(Float value) throws IOException {
    if (value == null) {
      writer.write("null");
    } else {
      writer.write(String.valueOf(value));
    }
  }

  private void writeValue(Double value) throws IOException {
    if (value == null) {
      writer.write("null");
    } else {
      writer.write(String.valueOf(value));
    }
  }


  private void writeKey(String key) throws IOException {
    writer.write('"');
    writer.write(key);
    writer.write('"');
    writer.write(':');
  }


  @Override
  public void visit(Organisation organisation) throws IOException {
    writer.write("{");
    writeIdentity(organisation);

    writeSourcedKeyValueString(true, "namn", organisation.getNamn());
    writeSourcedKeyValueString(true, "nummerPrefix", organisation.getNummerprefix());
    writeSourcedKeyValueString(true, "nummer", organisation.getNummer());
    writeSourcedKeyValueString(true, "nummerSuffix", organisation.getNummersuffix());
    writeSourcedKeyValueString(true, "firmaform", organisation.getFirmaform());
    writeSourcedKeyValueString(true, "lanIdentity", organisation.getLänIdentity());

    writer.write(',');
    writeKey("status");
    if (organisation.getStatus().isEmpty()) {
      writer.write("null");
    } else {
      writer.write('[');
      for (Iterator<SourcedValue<String>> iterator = organisation.getStatus().iterator(); iterator.hasNext(); ) {
        SourcedValue<String> status = iterator.next();
        writeSourcedValueString(false, status);
        if (iterator.hasNext()) {
          writer.write(',');
        }
      }
      writer.write(']');
    }

    if (writingMetadata) {
      writer.write(',');
      writeSourced(organisation);
    }


    writer.write("}");
  }


  @Override
  public void visit(Lan län) throws IOException {
    writer.write("{");
    writeIdentity(län);

    writeSourcedKeyValueString(true, "namn", län.getNamn());
    writeSourcedKeyValueString(true, "alfakod", län.getAlfakod());
    writeSourcedKeyValueString(true, "nummerkod", län.getNummerkod());

    if (writingMetadata) {
      writer.write(',');
      writeSourced(län);
    }

    writer.write("}");
  }

  @Override
  public void visit(Kommun kommun) throws IOException {
    writer.write("{");
    writeIdentity(kommun);

    writeSourcedKeyValueString(true, "namn", kommun.getNamn());
    writeSourcedKeyValueString(true, "nummerkod", kommun.getNummerkod());
    writeSourcedKeyValueString(true, "lanIdentity", kommun.getLänIdentity());

    if (writingMetadata) {
      writer.write(',');
      writeSourced(kommun);
    }

    writer.write("}");
  }

  @Override
  public void visit(Ort ort) throws IOException {
    writer.write("{");
    writeIdentity(ort);

    writeSourcedKeyValueString(true, "namn", ort.getNamn());
    writeSourcedKeyValueString(true, "tatortskod", ort.getTätortskod());
    writeSourcedKeyValueString(true, "kommunIdentity", ort.getKommunIdentity());

    if (writingMetadata) {
      writer.write(',');
      writeSourced(ort);
    }

    writer.write("}");
  }

  @Override
  public void visit(Postort postort) throws IOException {
    writer.write("{");
    writeIdentity(postort);

    writeSourcedKeyValueString(true, "namn", postort.getNamn());

    if (writingMetadata) {
      writer.write(',');
      writeSourced(postort);
    }

    writer.write("}");
  }

  @Override
  public void visit(Postnummer postnummer) throws IOException {
    writer.write("{");
    writeIdentity(postnummer);

    writeSourcedKeyValueString(true, "postnummer", postnummer.getPostnummer());
    writeSourcedKeyValueString(true, "postortIdentity", DomainStore.getInstance().getPostorter().get(postnummer.getPostortIdentity().get()).getNamn());

    writer.write(',');
    writeKey("stereotyper");
    if (postnummer.getStereotyper().isEmpty()) {
      writer.write("null");
    } else {
      writer.write('[');
      for (Iterator<SourcedValue<String>> iterator = postnummer.getStereotyper().iterator(); iterator.hasNext(); ) {
        SourcedValue<String> stereotyp = iterator.next();
        writeSourcedValueString(false, stereotyp);
        if (iterator.hasNext()) {
          writer.write(',');
        }
      }
      writer.write(']');
    }


    if (writingMetadata) {
      writer.write(',');
      writeSourced(postnummer);
    }

    writer.write("}");
  }

  @Override
  public void visit(Gata gata) throws IOException {
    writer.write("{");
    writeIdentity(gata);

    writeSourcedKeyValueString(true, "namn", gata.getNamn());
    writeSourcedKeyValueString(true, "postortIdentity", gata.getPostortIdentity());

    writer.write(',');
    writeKey("postnummerIdentity");
    if (gata.getPostnummerIdentities().isEmpty()) {
      writer.write("null");
    } else {
      writer.write('[');
      for (Iterator<SourcedValue<String>> iterator = gata.getPostnummerIdentities().iterator(); iterator.hasNext(); ) {
        SourcedValue<String> postnummer = iterator.next();
        writeSourcedValueString(false, postnummer);
        if (iterator.hasNext()) {
          writer.write(',');
        }
      }
      writer.write(']');
    }

    if (writingMetadata) {
      writer.write(',');
      writeSourced(gata);
    }

    writer.write("}");

  }

  @Override
  public void visit(Gatuadress gatuadress) throws IOException {
    writer.write("{");
    writeIdentity(gatuadress);

    writeSourcedKeyValueInteger(true, "gatunummer", gatuadress.getGatunummer());
    writeSourcedKeyValueString(true, "gataIdentity", gatuadress.getGataIdentity());
    writeSourcedKeyValueString(true, "postnummerIdentity", gatuadress.getPostnummerIdentity());

    if (writingMetadata) {
      writer.write(',');
      writeSourced(gatuadress);
    }

    writer.write("}");

  }

  @Override
  public void visit(Dokumentversion dokumentversion) throws IOException {
    throw new UnsupportedOperationException();
    // todo lean back on reflection for non implemented visiting methods?
  }

  @Override
  public void visit(Dokument dokument) throws IOException {
    throw new UnsupportedOperationException();
    // todo lean back on reflection for non implemented visiting methods?
  }

  @Override
  public void visit(Stadgar stadgar) throws IOException {
    throw new UnsupportedOperationException();
    // todo lean back on reflection for non implemented visiting methods?
  }

  @Override
  public void visit(EkonomiskPlan ekonomiskPlan) throws IOException {
    throw new UnsupportedOperationException();
    // todo lean back on reflection for non implemented visiting methods?
  }

  @Override
  public void visit(Arsredovisning årsredovisning) throws IOException {
    throw new UnsupportedOperationException();
    // todo lean back on reflection for non implemented visiting methods?
  }
}
