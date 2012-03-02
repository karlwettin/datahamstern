package se.datahamstern.util;

/**
 * @author http://www.cse.chalmers.se/~skanshol/javasteg/steg2/uppgifter/kap7/uppg7-8.txt
 * @author kalle
 * @since 2009-okt-04 22:19:32
 */
public class Mod10 {

  public static boolean isValidSwedishOrganizationNumber(String organizationNumber) {
    if (organizationNumber.length() != 10) {
      return false;
    }
    for (char c : organizationNumber.toCharArray()) {
      if (c < '0' || c > '9') {
        return false;
      }
    }
    int sum = 0;
    for (int i = 0; i < 9; i++) {
      int tal = Integer.parseInt(organizationNumber.substring(i, i + 1));
      int j = tal * (2 - i % 2);   // multiplicera med 2 eller 1
      sum += j / 10 + j % 10;      // addera siffrorna i resultatet till summan
    }
    sum %= 10;
    return !((Integer.parseInt(organizationNumber.substring(9, 10)) + sum) % 10 != 0);
  }



}
