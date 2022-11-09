import org.bouncycastle.jcajce.provider.keystore.BCFKS;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Scanner;


public class Encrypto {

    private static String TRIPLE_DES_TRANSFORMATION = "DESede/ECB/PKCS7Padding";
    private static String ALGORITHM = "DESede";
    private static String BOUNCY_CASTLE_PROVIDER = "BC";
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String PASSWORD_HASH_ALGORITHM = "SHA";

    public static void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /*
     * To do : encrypt plaintext using 3Des algorithm
     */
    private static byte[] encode(byte[] input, String key) throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
        init();
        Cipher encrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);

        encrypter.init(Cipher.ENCRYPT_MODE, buildKey(key.toCharArray()));
        //encrypt
        return encrypter.doFinal(input);
    }

    /*
     * To do : decrypt plaintext using 3Des algorithm
     */
    public static byte[] decode(byte[] input, String key) throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
        init();
        Cipher decrypter = Cipher.getInstance(TRIPLE_DES_TRANSFORMATION, BOUNCY_CASTLE_PROVIDER);

        decrypter.init(Cipher.DECRYPT_MODE, buildKey(key.toCharArray()));

        return decrypter.doFinal(input);
    }

    private static Key buildKey(char[] password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        init();
        MessageDigest digester = MessageDigest.getInstance(PASSWORD_HASH_ALGORITHM);
        digester.update(String.valueOf(password).getBytes(UNICODE_FORMAT));
        byte[] key = digester.digest();

        //3des key using 24 byte, convert to 24 byte
        byte[] keyDes = Arrays.copyOf(key, 24);
        SecretKeySpec spec = new SecretKeySpec(keyDes, ALGORITHM);
        return spec;
    }

    public static byte[] getByte(String string) throws UnsupportedEncodingException {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String getString(byte[] byteText) {
        return new String(byteText,StandardCharsets.UTF_8);
    }


    public static void main(String[] args) {
        Scanner reader = null;
        try {
            reader = new Scanner(new File("resources/users.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder o = new StringBuilder();
        while (reader.hasNextLine())
        {
            String s = reader.nextLine();
            try {
                 byte[] temp = encode(getByte(s),"bananananana");
                 for(byte b: temp)
                 {
                     int bInt = b & 0xff;
                     System.out.println("Converting byte:" + b + " to:" + bInt);
                     o.append(bInt).append("|");
                 }
                 o.append("\n");

            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream("resources/enc.file"));;
            writer.write(o.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
