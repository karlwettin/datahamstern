package se.datahamstern.domain;

import com.sleepycat.persist.model.Persistent;
import se.datahamstern.sourced.SourcedValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2012-05-03 12:02
 */
@Persistent(version = 1)
public class Demografi {

  private int år;

  private SourcedValue<Integer> döda = new SourcedValue<Integer>();
  private SourcedValue<Integer> emigrerade = new SourcedValue<Integer>();
  private SourcedValue<Integer> födda = new SourcedValue<Integer>();
  private SourcedValue<Integer> immigrationsnetto = new SourcedValue<Integer>();
  private SourcedValue<Integer> immigrerade = new SourcedValue<Integer>();
  private SourcedValue<Integer> inflyttade = new SourcedValue<Integer>();
  private SourcedValue<Integer> inrikesNettoinflyttning = new SourcedValue<Integer>();
  private SourcedValue<Integer> invånare = new SourcedValue<Integer>();
  private SourcedValue<Integer> utflyttade = new SourcedValue<Integer>();

  private Map<Integer, SourcedValue<Integer>> befolkningsandelarByÅlder = new HashMap<Integer, SourcedValue<Integer>>();

  @Override
  public String toString() {
    return "Demografi{" +
        "år=" + år +
        ", döda=" + döda +
        ", emigrerade=" + emigrerade +
        ", födda=" + födda +
        ", immigrationsnetto=" + immigrationsnetto +
        ", immigrerade=" + immigrerade +
        ", inflyttade=" + inflyttade +
        ", inrikesNettoinflyttning=" + inrikesNettoinflyttning +
        ", invånare=" + invånare +
        ", utflyttade=" + utflyttade +
        ", befolkningsandelarByÅlder=" + befolkningsandelarByÅlder +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Demografi demografi = (Demografi) o;

    if (år != demografi.år) return false;
    if (befolkningsandelarByÅlder != null ? !befolkningsandelarByÅlder.equals(demografi.befolkningsandelarByÅlder) : demografi.befolkningsandelarByÅlder != null) return false;
    if (döda != null ? !döda.equals(demografi.döda) : demografi.döda != null) return false;
    if (emigrerade != null ? !emigrerade.equals(demografi.emigrerade) : demografi.emigrerade != null) return false;
    if (födda != null ? !födda.equals(demografi.födda) : demografi.födda != null) return false;
    if (immigrationsnetto != null ? !immigrationsnetto.equals(demografi.immigrationsnetto) : demografi.immigrationsnetto != null) return false;
    if (immigrerade != null ? !immigrerade.equals(demografi.immigrerade) : demografi.immigrerade != null) return false;
    if (inflyttade != null ? !inflyttade.equals(demografi.inflyttade) : demografi.inflyttade != null) return false;
    if (inrikesNettoinflyttning != null ? !inrikesNettoinflyttning.equals(demografi.inrikesNettoinflyttning) : demografi.inrikesNettoinflyttning != null) return false;
    if (invånare != null ? !invånare.equals(demografi.invånare) : demografi.invånare != null) return false;
    if (utflyttade != null ? !utflyttade.equals(demografi.utflyttade) : demografi.utflyttade != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = år;
    result = 31 * result + (döda != null ? döda.hashCode() : 0);
    result = 31 * result + (emigrerade != null ? emigrerade.hashCode() : 0);
    result = 31 * result + (födda != null ? födda.hashCode() : 0);
    result = 31 * result + (immigrationsnetto != null ? immigrationsnetto.hashCode() : 0);
    result = 31 * result + (immigrerade != null ? immigrerade.hashCode() : 0);
    result = 31 * result + (inflyttade != null ? inflyttade.hashCode() : 0);
    result = 31 * result + (inrikesNettoinflyttning != null ? inrikesNettoinflyttning.hashCode() : 0);
    result = 31 * result + (invånare != null ? invånare.hashCode() : 0);
    result = 31 * result + (utflyttade != null ? utflyttade.hashCode() : 0);
    result = 31 * result + (befolkningsandelarByÅlder != null ? befolkningsandelarByÅlder.hashCode() : 0);
    return result;
  }

  public int getÅr() {
    return år;
  }

  public void setÅr(int år) {
    this.år = år;
  }

  public SourcedValue<Integer> getDöda() {
    return döda;
  }

  public void setDöda(SourcedValue<Integer> döda) {
    this.döda = döda;
  }

  public SourcedValue<Integer> getEmigrerade() {
    return emigrerade;
  }

  public void setEmigrerade(SourcedValue<Integer> emigrerade) {
    this.emigrerade = emigrerade;
  }

  public SourcedValue<Integer> getFödda() {
    return födda;
  }

  public void setFödda(SourcedValue<Integer> födda) {
    this.födda = födda;
  }

  public SourcedValue<Integer> getImmigrationsnetto() {
    return immigrationsnetto;
  }

  public void setImmigrationsnetto(SourcedValue<Integer> immigrationsnetto) {
    this.immigrationsnetto = immigrationsnetto;
  }

  public SourcedValue<Integer> getImmigrerade() {
    return immigrerade;
  }

  public void setImmigrerade(SourcedValue<Integer> immigrerade) {
    this.immigrerade = immigrerade;
  }

  public SourcedValue<Integer> getInflyttade() {
    return inflyttade;
  }

  public void setInflyttade(SourcedValue<Integer> inflyttade) {
    this.inflyttade = inflyttade;
  }

  public SourcedValue<Integer> getInrikesNettoinflyttning() {
    return inrikesNettoinflyttning;
  }

  public void setInrikesNettoinflyttning(SourcedValue<Integer> inrikesNettoinflyttning) {
    this.inrikesNettoinflyttning = inrikesNettoinflyttning;
  }

  public SourcedValue<Integer> getInvånare() {
    return invånare;
  }

  public void setInvånare(SourcedValue<Integer> invånare) {
    this.invånare = invånare;
  }

  public SourcedValue<Integer> getUtflyttade() {
    return utflyttade;
  }

  public void setUtflyttade(SourcedValue<Integer> utflyttade) {
    this.utflyttade = utflyttade;
  }

  public Map<Integer, SourcedValue<Integer>> getBefolkningsandelarByÅlder() {
    return befolkningsandelarByÅlder;
  }

  public void setBefolkningsandelarByÅlder(Map<Integer, SourcedValue<Integer>> befolkningsandelarByÅlder) {
    this.befolkningsandelarByÅlder = befolkningsandelarByÅlder;
  }
}
