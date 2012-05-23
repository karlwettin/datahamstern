Ett par meningar om hur jag tänkte att det skulle fungera.

Databasen med det för närvarande senast kända innehållet är ett resultatet av att spela upp en uppsättning händelsetransaktioner.

För det mesta är en händelse ett kommando om att uppdatera en post i databasen.

Alla händelser kan associeras med källhänvisningar, licenser och tillförlitlighet vilket man även kan filtrera på när man bygger databasen.

Nya händelser läggs till exempelvis av robotar som gruvar i den existerande informationsmängden, skördeströskor och människor.

Man byter enkelt händelser med varandra, exempelvis via git.

Servern distribueras som en Linux image med allt färdiginstallerat, redo att startas upp i VirtualBox.

Debian 6, Java, Berkeley DB, Lucene, Selenium.



Så här skulle en extremt utförlig transaktion kunna se ut från en robot som går till wikipedia för att hämta information om län och sedan lägger till en geografisk gräns genom att  söka på open street maps nominatim med det på wikipedia funna länsnamnet:

{
  "transactions" : [
    {
      "command" : {
        "name" : "uppdatera län",
        "version" : 1 /* bakåtkompatibilitet om logiken som skriver händelsen till databasen förändras. */
      },
      "sources" : [{
        "identity" : "janne1965@wikipedia/se",
        "trustworthiness" : 0.97,
        "details" : "ingen har ändrat på artikeln sedan tre månader då janne1965 städade efter ett troll",
        "licence" : "public domain",
        "timestamp" : 1112662622223 /* datumet när janne1965 tryckte på spara-knappen. */
      }],
      "data" : {
          "sifferkod" : "01",
          "bokstavskod" : "A",
          "namn" : "Stockholms län",
        }
      }
    },
    {
      "transaction" : {
        "type" : "uppdatera län",
        "version" : 1
      },
      "sources" : [{
        "identity" : "open street map/nominatim",
        "trustworthiness" : 0.3,
        "details" : "geocodade 'stockholms län, sverige' som jag fick från wikipedia. resultatet blev en fyrkant.",
        "licence" : "creative commons",
        "timestamp" : 1112834578362
      }, {
        "identity" : "janne1965@wikipedia/se",
        "trustworthiness" : 0.97,
        "details" : "ingen har ändrat på artikeln sedan tre månader då janne1965 städade efter ett troll",
        "licence" : "public domain",
        "timestamp" : 1112662622223
      }],
      "data" : {
        "sifferkod" : "01",
        "bokstavskod" : "A",
        "polyogon" :[[54.1, 16.2], [54.9, 16.21], [54.2, 16.3], [54.4, 16.5]
        }
      },
    }
  ]
}



			kalle
			
			
			
