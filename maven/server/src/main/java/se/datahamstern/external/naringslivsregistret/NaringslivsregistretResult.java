package se.datahamstern.external.naringslivsregistret;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2012-02-29 01:20
 */
public class NaringslivsregistretResult implements Serializable {

  private static final long serialVersionUID = 1l;


  /**
   * 2 siffor
   * <p/>
   * http://sv.wikipedia.org/wiki/Organisationsnummer
   * 12-siffriga organisationsnummer/personnummer:
   * för juridisk person: 16 + företagets tilldelade organisationsnummer (10 siffror),
   * för fysisk person: sekelsiffror 18, 19 el. 20 + personnummer (10 siffror).
   */
  private String nummerprefix;

  /**
   * 10 siffror
   * <p/>
   * http://sv.wikipedia.org/wiki/Organisationsnummer
   * Den första siffran i organisationsnumret anger vilken grupp av företagsformer organisationen tillhör, nämligen:
   * 2 – Stat, landsting, kommuner, församlingar
   * 5 – Aktiebolag
   * 6 – Enkelt bolag
   * 7 – Ekonomiska föreningar
   * 8 – Ideella föreningar och stiftelser
   * 9 – Handelsbolag, kommanditbolag och enkla bolag
   */
  private String nummer;

  /**
   * 3 siffror?
   * samma person med flera enkilda firmor har suffix.
   */
  private String nummersuffix;
  private String namn;

  /**
   * https://snr4.bolagsverket.se/snrgate/hjalp.do?method=hjalp.foretagsformer
   * Näringslivsregistret - Företagsformer
   * <p/>
   * Möjliga företagsformer är
   * <p/>
   * AB 	Aktiebolag
   * BAB 	Bankaktiebolag
   * BF 	Bostadsförening
   * BFL 	Utländsk banks filial
   * BRF 	Bostadsrättsförening
   * E 	Enskild näringsidkare / Enskild firma
   * EGTS 	Europeiska Grupperingar för Territoriellt Samarbete
   * EK 	 Ekonomisk förening
   * FAB 	 Försäkringsaktiebolag
   * FL 	 Filial
   * FOF 	 Försäkringsförening
   * HB 	 Handelsbolag
   * I 	Ideell förening som bedriver näring
   * KB 	Kommanditbolag
   * KHF 	Kooperativ hyresrättsförening
   * MB 	Medlemsbank
   * OFB 	Ömsesidigt försäkringsbolag
   * SB 	Sparbank
   * SCE 	Europakooperativ
   * SE 	Europabolag
   * SF 	Sambruksförening
   * S 	Stiftelse som bedriver näring
   * TSF 	Trossamfund
   */
  private String firmaform;

  /**
   * todo osäker på hur detta hänger ihop, är det samma organisationsnummer med olika namn?
   * todo lagras för närvarande inte i bdb.
   *
   * https://snr4.bolagsverket.se/snrgate/hjalp.do?method=hjalp.firmatyper
   * Näringslivsregistret - Firmatyper
   * Följande firmatyper kan visas:
   * Firma: Företagsnamn
   * Bifirma: Ett extra företagsnamn för en del av företagets verksamhet
   * Parallellfirma: Företagets namn (firma) översatt till annat språk
   */
  private String firmatyp;

  /**
   * https://snr4.bolagsverket.se/snrgate/hjalp.do?method=hjalp.lanskoder
   * Näringslivsregistret - Länskoder
   * AB 	01 	Stockholms län
   * C 	03 	Uppsala län
   * D 	04 	Södermanlands län
   * E 	05 	Östergötlands län
   * F 	06 	Jönköpings län
   * G 	07 	Kronobergs län
   * H 	08 	Kalmar län
   * I 	09 	Gotlands län
   * K 	10 	Blekinge län
   * M 	12 	Skåne län
   * N 	13 	Hallands län
   * O 	14 	Västra Götalands län
   * S 	17 	Värmlands län
   * T 	18 	Örebro län
   * U 	19 	Västmanlands län
   * W 	20 	Dalarnas län
   * X 	21 	Gävleborgs län
   * Y 	22 	Västernorrlands län
   * Z 	23 	Jämtlands län
   * AC 	24 	Västerbottens län
   * BD 	25 	Norrbottens län
   */
  private String länsnummer;

  /**
   * https://snr4.bolagsverket.se/snrgate/hjalp.do?method=hjalp.foretagsstatus
   * Näringslivsregistret - Företagsstatus
   * Statuskoder
   * <p/>
   * De statuskoder som visas med fet stil innebär att företaget inte längre finns
   * <p/>
   * Statuskod 	Kortnamn 	Förklaring
   * AC 11 	ACKORDSFÖRHANDLING INLEDD
   * AC 12 	ACKORDSFÖRHANDLING UPPHÖRD
   * AC 13 	ACKORDSFÖRHANDLING UPPHÄVD AV DOMSTOL
   * KK 20 	KONKURS INLEDD 	företaget är under avveckling
   * KK 21 	KONKURS AVSLUTAD 	företaget har upphört att existera
   * KK 22 	KONKURS AVSLUTAD MED ÖVERSKOTT 	vilket innebär att det fanns tillgångar kvar i bolaget efter det att konkursen avslutades
   * KK 24 	KONKURS UPPHÄVD AV RÄTT 	av någon anledning har konkursen upphävts av en domstol
   * LI 31 	LIKVIDATION AVSLUTAD
   * LI 32 	LIKVIDATION BESLUTAD
   * LI 33 	LIKVIDATION FORTSÄTTER 	i ett företag som tidigare har avslutats med likvidation
   * LI 34 	LIKVIDATION UPPHÖR, VERKSAMHETEN ÅTERUPPTAS. 	företaget kan under en s k frivillig likvidation återuppta verksamheten och upphäva likvidationen
   * LI 35 	LIKVIDATION UPPHÄVD AV DOMSTOL
   * LI 36 	BOLAGET AVFÖRT ENL 13 KAP 18 § ABL 	avförd av Bolagsverket p g a att bolaget inte har ändrat i sina registrerade uppgifter på de senaste 10 åren
   * LI 37 	BOLAGET ÄR AVFÖRT 	har avförts ur aktiebolagsregistret och blivit ett bankaktiebolag eller så har det avförts p g a att bolaget inte höjde sitt aktiekapital till 50 000 kr.
   * FU 40 	FUSION INLEDD
   * FU 41 	UPPLÖST GENOM FUSION
   * <p/>
   * FU 45 	FUSION TILLÅTEN 	fusionen har efter ett bestridande tillåtits av Bolagsverket eller domstol
   * FU 49 	FUSION PÅGÅR 	under fusion pågår markeras det övertagande bolaget med koden 49
   * AF 50 	AVFÖRD ENLIGT 17 § 2 ST HANDELS- REGISTERLAGEN 	avfört av Bolagsverket p g a att bolaget inte har ändrat i sina registrerade uppgifter på de senaste 10 åren
   * AF 51 	AVFÖRD ENLIGT 11 KAP 18 § LAG OM EK.FÖRENINGAR 	avförd av Bolagsverket p g a att föreningen inte har ändrat i sina registrerade uppgifter på de senaste 10 åren
   * AR 52 	AVREGISTRERAD 	p g a verksamheten upphört
   * AR 53 	AVREGISTRERAD P G A NY INNEHAVARE 	till den enskilda firman
   * AF 54 	AVFÖRD P G A FUSION MED UTLÄNDSKT FÖRETAG
   * AF 60 	AVFÖRD P G A UTLÄNDSKT FÖRETAGS LIKVIDATION/KONKURS 	filialens utländska företag har gått i likvidation eller konkurs
   * AF 61 	AVFÖRD, VERKSAMHETEN HAR UPPHÖRT 	filialens verksamhet har upphört i Sverige
   * AF 62 	AVFÖRD, FILIALEN SAKNAR VERKSTÄLLANDE DIREKTÖR
   * AF 63 	AVFÖRD, ENLIGT DOMSTOLSBESLUT 	en domstol har beslutat att filialen skall avföras ur registret
   * AF 64 	AVFÖRD, ÅRSREDOVISNING SAKNAS 	filialen har inte skickat in sin årsredovisning till Bolagsverket
   * AF 70 	BOLAGET AVFÖRT PÅ EGEN BEGÄRAN 	gäller bolag där aktiekapitalet inte ökat till 100 000 kr
   * AF 71 	BOLAGET AVFÖRT AV BOLAGSVERKET 	gäller bolag där aktiekapitalet inte ökat till 100 000 kr
   * AF 73 	AVFÖRD
   * AF 74 	AVFÖRD, OMREGISTRERAT TILL BANKAKTIEBOLAG
   * AF 75 	BESLUT OM OMBILDNING
   * AF 76 	TILLSTÅND TILL OMBILDNING
   * AF 77 	AVREGISTRERAD P G A OMBILDNING
   * AF 78 	OMBILDNING FÖRFALLEN
   * FR 80 	FÖRETAGSREKONSTRUKTION INLEDD 	rekonstruktion är beslutad
   * FR 81 	FÖRETAGSREKONSTRUKTION UPPHÖRD 	rekonstruktionen har upphört och företaget återgår till sin verksamhet under förutsättning att inte konkurs inleds
   * FR 82 	FÖRETAGSREKONSTRUKTION UPPHÄVD AV DOMSTOL
   * DE 90 	DELNING PÅGÅR
   * DE 91 	UPPLÖST GENOM DELNING
   * DE 99 	ÖVERTAGANDE AV ANNAT BOLAG PÅGÅR
   */
  private String status;

  public void setNummerprefix(String nummerprefix) {
    this.nummerprefix = nummerprefix;
  }

  public void setNummer(String nummer) {
    this.nummer = nummer;
  }

  public void setNummersuffix(String nummersuffix) {
    this.nummersuffix = nummersuffix;
  }

  public void setNamn(String namn) {
    this.namn = namn;
  }

  public void setFirmaform(String firmaform) {
    this.firmaform = firmaform;
  }

  public void setLänsnummer(String länsnummer) {
    this.länsnummer = länsnummer;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getNummerprefix() {
    return nummerprefix;
  }

  public String getNummer() {
    return nummer;
  }

  public String getNummersuffix() {
    return nummersuffix;
  }

  public String getNamn() {
    return namn;
  }

  public String getFirmaform() {
    return firmaform;
  }

  public String getLänsnummer() {
    return länsnummer;
  }

  public String getStatus() {
    return status;
  }

  public String getFirmatyp() {
    return firmatyp;
  }

  public void setFirmatyp(String firmatyp) {
    this.firmatyp = firmatyp;
  }

  @Override
  public String toString() {
    return "NaringslivsregistretResult{" +
        "nummerprefix='" + nummerprefix + '\'' +
        ", nummer='" + nummer + '\'' +
        ", nummersuffix='" + nummersuffix + '\'' +
        ", namn='" + namn + '\'' +
        ", firmaform='" + firmaform + '\'' +
        ", firmatyp='" + firmatyp + '\'' +
        ", länsnummer='" + länsnummer + '\'' +
        ", status='" + status + '\'' +
        '}';
  }
}
