import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    // Converts a decimal string to 16 bit binary string
    private static String decToBinStr(String num) {
        String res = Integer.toBinaryString(Integer.parseInt(num));
        while (res.length() < 16) {
            res = "0" + res;
        }
        return res;
    }

    // Checks if a number is prime or not
    private static boolean isPrime(int n) {
        if (n <= 1)
            return false;
        for (int i = 2; i < n; i++)
            if (n % i == 0)
                return false;
        return true;
    }

    // Calculates gcd of integers using eucledian algorithm
    private static int gcd(int a, int b) {
        if (b == 0)
            return a;
        return gcd(b, a % b);
    }

    // Returns the first three intergers of digest
    private static BigInteger filterDigest(BigInteger digest, BigInteger n) {
        String s = String.valueOf(digest);
        if (s.length() >= 3) {
            s = s.substring(0, 3);
        }
        int temp = Integer.parseInt(s);
        while (temp > Integer.parseInt(n.toString())) {
            temp /= 10;
        }
        BigInteger modifiedDigest = new BigInteger(String.valueOf(temp));
        return modifiedDigest;
    }

    public static void main(String[] args) {
        try {
            System.out.println("Name: Garvit Gupta");
            System.out.println("Roll No: 2019060\n\n");

            // Intializing server socket
            Socket socket = new Socket("localhost", 8080);
            BufferedReader dataFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Taking public key parameters
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter you message: ");
            String message = sc.nextLine();
            System.out.println("Enter secret key: ");
            String secretKey = sc.nextLine();
            System.out.println("Enter p: ");
            int p = sc.nextInt();
            System.out.println("Enter q: ");
            int q = sc.nextInt();
            System.out.println("Enter e: ");
            int _e = sc.nextInt();

            // Check if these inputs are valid or not
            if (!isPrime(p) || !isPrime(q)) {
                System.out.println("p and q must be a prime number\nPlease try again");
                System.exit(0);
            }
            if (gcd(_e, (p - 1) * (q - 1)) != 1) {
                System.out.println("Make sure that gcd of e and (p-1)*(q-1) should be 1");
                System.exit(0);
            }

            // Encrypt the secret key using RSA algorithm
            BigInteger secret_key = new BigInteger(secretKey);
            RSA rsa = new RSA();
            int[] keys = rsa.key_genrator(p, q, _e);

            BigInteger e = new BigInteger(Integer.toString(keys[0]));
            BigInteger d = new BigInteger(Integer.toString(keys[1]));
            BigInteger n = new BigInteger(Integer.toString(keys[2]));

            // Receiving the server public key from server
            BigInteger server_e = new BigInteger(dataFromServer.readLine());
            BigInteger server_n = new BigInteger(dataFromServer.readLine());

            // Encrypting the secret key using server public keys
            BigInteger encryptedKey = rsa.encryption(secret_key, server_e, server_n);
            System.out.println("Encrypted Secret Key: " + encryptedKey);

            // Encrypting the message using AES
            System.out.println("Cipher text intermediate computation process:");
            AES aes = new AES();
            String cipherText = aes.encryption(decToBinStr(message), decToBinStr(secretKey));
            System.out.println("Cipher text: " + Integer.parseInt(cipherText, 2));

            // Hashing the message to genrate digest
            Hash hash = new Hash();
            String digest = hash.encryptThisString(message);
            System.out.println("Digest: " + digest);

            // Processing the digest and then encrypting it using RSA
            BigInteger processedDigest = filterDigest(new BigInteger(digest, 16), n);
            BigInteger signature = rsa.encryption(processedDigest, d, n);
            System.out.println("Digital Signature: " + signature);

            // Sending the data to client
            PrintWriter dataToServer = new PrintWriter(socket.getOutputStream(), true);
            dataToServer.println(cipherText);
            dataToServer.println(encryptedKey);
            dataToServer.println(signature);
            dataToServer.println(_e);
            dataToServer.println(keys[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
