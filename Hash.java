import java.math.BigInteger;
import java.security.*;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static String encryptThisString(String input) {
        try {
            // Use MD2 algorithm to genrate digest
            MessageDigest md = MessageDigest.getInstance("MD2");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            // Convert digest to hexString
            String hashtext = no.toString(16);

            // Make sure that length of hexString is 32
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}