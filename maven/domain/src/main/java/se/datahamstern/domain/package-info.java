/**
 * Domänklasser ligger i subpaket namngedda sina källor.
 *
 * Så är det för att jag har en kuslig känsla för att spå i framtiden
 * då man med all säkerhet kommer stöta på trupp vid försök att konsolidera data mellan olika källor
 * och vad dessa käller innehåller för information kommer ändras över tiden.
 *
 * Man kommer vilja ha det ytterligare ett domänlager som är helt frikopplat från olika källor
 * men det tar tid att skriva och jag orkar inte just nu så blev det ett och samma lager.
 * Jag har bara valt att dela upp det i paket på där här viset så jag inte glömmer bort
 * anledningen till det.
 *
 * Därför är det nu lite kladdigt att exempelvis
 * det finns hårda associationer mellan klasser som beskriver data från posten
 * och klasser som beskriver data från wikipedia.
 *
 */
package se.datahamstern.domain;