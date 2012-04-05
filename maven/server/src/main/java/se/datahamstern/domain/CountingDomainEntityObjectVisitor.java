package se.datahamstern.domain;

import se.datahamstern.domain.hydda.*;
import se.datahamstern.domain.naringslivsregistret.Organisation;
import se.datahamstern.domain.postnummer.Gata;
import se.datahamstern.domain.postnummer.Gatuadress;
import se.datahamstern.domain.postnummer.Postnummer;
import se.datahamstern.domain.postnummer.Postort;
import se.datahamstern.domain.wikipedia.Kommun;
import se.datahamstern.domain.wikipedia.Lan;
import se.datahamstern.domain.wikipedia.Ort;
import se.datahamstern.event.Event;
import se.datahamstern.util.EggClockTimer;
import se.datahamstern.util.ScoreMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * For debug use, tracks number of puts to the bdb.
 *
 * @author kalle
 * @since 2012-04-02 21:59
 */
public class CountingDomainEntityObjectVisitor extends DecoratedDomainEntityObjectVisitor<Exception> {
  
  private ScoreMap<Class> counters;
  private AtomicLong totalCounter = new AtomicLong();
  private AtomicLong totalCounterSinceAlarm = new AtomicLong();

  private EggClockTimer debugTimer = new EggClockTimer(TimeUnit.SECONDS.toMillis(10)){
    @Override
    public void alarm() {
      if (totalCounterSinceAlarm.get() > 0) {
        System.out.println(totalCounter + " domain entity objects put in total, " + totalCounterSinceAlarm.get() +  " since previous alarm. Details: " + counters.toString());
        totalCounterSinceAlarm.set(0);
      }
    }
  };

  public CountingDomainEntityObjectVisitor(DomainEntityObjectVisitor decorated) {
    super(decorated);
    counters = new ScoreMap<Class>();
  }

  private void incrementAndGet(Class type) {
    totalCounter.incrementAndGet();
    totalCounterSinceAlarm.incrementAndGet();
    counters.increaseAndGet(type);
  }

  @Override
  protected void finalize() throws Throwable {
    debugTimer.stop();
  }

  @Override
  public void visit(Organisation organisation)  throws Exception {
    super.visit(organisation);
    incrementAndGet(organisation.getClass());
  }

  @Override
  public void visit(Lan län)  throws Exception{
    super.visit(län);
    incrementAndGet(län.getClass());
  }

  @Override
  public void visit(Kommun kommun)  throws Exception{
    super.visit(kommun);
    incrementAndGet(kommun.getClass());
  }

  @Override
  public void visit(Ort ort)  throws Exception{
    super.visit(ort);
    incrementAndGet(ort.getClass());
  }

  @Override
  public void visit(Gatuadress gatuaddress) throws Exception{
    super.visit(gatuaddress);
    incrementAndGet(gatuaddress.getClass());
  }

  @Override
  public void visit(Gata gata) throws Exception{
    super.visit(gata);
    incrementAndGet(gata.getClass());
  }

  @Override
  public void visit(Postnummer postnummer) throws Exception{
    super.visit(postnummer);
    incrementAndGet(postnummer.getClass());
  }

  @Override
  public void visit(Postort postort) throws Exception{
    super.visit(postort);
    incrementAndGet(postort.getClass());
  }

  @Override
  public void visit(Arsredovisning årsredovisning) throws Exception{
    super.visit(årsredovisning);
    incrementAndGet(årsredovisning.getClass());
  }

  @Override
  public void visit(EkonomiskPlan ekonomiskPlan) throws Exception{
    super.visit(ekonomiskPlan);
    incrementAndGet(ekonomiskPlan.getClass());
  }

  @Override
  public void visit(Stadgar stadgar) throws Exception{
    super.visit(stadgar);
    incrementAndGet(stadgar.getClass());
  }

  @Override
  public void visit(Dokument dokument) throws Exception{
    super.visit(dokument);
    incrementAndGet(dokument.getClass());
  }

  @Override
  public void visit(Dokumentversion dokumentversion) throws Exception{
    super.visit(dokumentversion);
    incrementAndGet(dokumentversion.getClass());
  }


}
