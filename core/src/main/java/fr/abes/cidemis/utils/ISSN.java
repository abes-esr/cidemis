package fr.abes.cidemis.utils;

/**
 * Classe d'utilitaire concernant les ISSN
 */
public class ISSN {
    /**
     * No instansiable class
     */
    private ISSN() {
        
    }
    
    /**
     * Retourne vrai si l'issn est valide    
     * @param issn
     * @return
     */
    public static boolean isValidISSN(String issn) {
        int sum = 0;
        String issnVerif = issn.replace("-","");
        
        // Calculate the sum of the 8 seven digits of the ISSN multiplied by its position in the number, counting from the right
        for (int j = 0; j < issnVerif.length(); j++) {
            String sub = issnVerif.substring(j, j+1);
            Integer digit = Integer.parseInt("X".equals(sub) ? "10" : sub);
            sum += digit * (8 - j);
        }
        
        //The modulus 11 of this sum is then calculated
        int modulus = sum % 11;
        
        return modulus == 0;
    }
}
