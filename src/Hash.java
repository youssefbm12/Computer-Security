import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

    //Source : https://www.javainterviewpoint.com/java-salted-password-hashing/#:~:text=As%20per%20OWASP%2C%20Salt%20should%20be%20generated%20using,the%20nextByte%20%28%29%20method%20generates%20the%20random%20salt.
public class Hash {
    public static void main (String[]args){
        //Main method, to test
        //Set a password
        String password  = "Imanol";
        //Get its storable version
        String storable = getStorable(password);
        //Check if "Imanol" corresponds to storable
        System.out.println(check("Imanol",storable));
    }

        /**
         * Method check : used to verify if input password corresponds to stored string (id/password)
         * @param s input string
         * @param hash stored string
         * @return true/false : does the string correspond to what is stored
         */
    public static boolean check(String s, String hash){
        //Split salt & hash
        String[] info = hash.split("\\$");
        //Store them
        String salt = info[0];
        String toDeHash = info[1];
        //Compare
        return hashString(salt +  s).equals(toDeHash);
    }
        /**
         * Method getStorable : call this method when writing JSON file with sensitive info
         * (e.g. don't write "Password" but getStorable("Password) in JSON file
         * @param s plaintext string (id/password)
         * @return String representing (salt + hash(salt + s)), safe to store in a database
         */
    public static String getStorable(String s){
        //Define salt
        String salt = getSalt().toString();
        //Define hash of salt + s
        String hash = hashString(salt+s);
        //Return salt + hash
        return salt + "$" + hash;
    }
    /**
     * Method getSalt : creates a random salt
     * @return byte[] containing random salt
     */
    public static byte[] getSalt()  {
        //Create salt
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Method hashString
     * @param s string to be hashed
     * @return String s hashed by SHA-256 algorithm
     */
    public static String hashString(String s){
        try{
            //Call SHA-256 algortihm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] toHash = s.getBytes(StandardCharsets.UTF_8);

            //Define encoded hash
            byte[] encodedhash = digest.digest(toHash);
            //Convert to hexadecimal
            return bytesToHex(encodedhash);

        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Method bytesToHex : converts bytes to hexadecimals
     * @param hash bytes to convert
     * @return hexadecimal equivalent
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
