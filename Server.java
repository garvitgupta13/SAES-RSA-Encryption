import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Server {
    //Convert decimal to binary string of 16 bit
    private static String decToBinStr(String num) {
        String res = Integer.toBinaryString(Integer.parseInt(num));
        while (res.length() < 16) {
            res = "0" + res;
        }
        return res;
    }

    //Check if a number is prime or not
    private static boolean isPrime(int n) {
        if (n <= 1)
            return false;
        for (int i = 2; i < n; i++)
            if (n % i == 0)
                return false;
        return true;
    }

    //Calculate gcd of integers
    private static int gcd(int a, int b) {
        if (b == 0)
            return a;
        return gcd(b, a % b);
    }

    public static void main(String[] args) {
        try {
            System.out.println("Name: Garvit Gupta");
            System.out.println("Roll No: 2019060\n\n");
            // Intializing server socket
            ServerSocket serverSocket = new ServerSocket(8080);
            Scanner sc = new Scanner(System.in);

            // Taking public key parameters
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

            // Establish the server client connection
            Socket socket = serverSocket.accept();
            PrintWriter dataToClient = new PrintWriter(socket.getOutputStream(), true);

            // Genrate the public and private keys of server
            RSA rsa = new RSA();
            int[] keys = rsa.key_genrator(p, q, _e);

            // Sending the server public key to client
            dataToClient.println(keys[0]);
            dataToClient.println(keys[2]);

            // Getting data from client
            BufferedReader dataFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String cipherText = dataFromClient.readLine();
            BigInteger encryptedKey = new BigInteger(dataFromClient.readLine());
            BigInteger clientSignature = new BigInteger(dataFromClient.readLine());
            BigInteger client_e = new BigInteger(dataFromClient.readLine());
            BigInteger client_n = new BigInteger(dataFromClient.readLine());

            BigInteger d = new BigInteger(Integer.toString(keys[1]));
            BigInteger n = new BigInteger(Integer.toString(keys[2]));

            // Decrypting the secret key
            BigInteger decryptedKey = rsa.encryption(encryptedKey, d, n);
            System.out.println("Decrypted Secret key: " + decryptedKey);

            // Performing AES decryption over cipherText received from client
            System.out.println("Decryption Intermediate process:");
            AES aes = new AES();
            String plainText = aes.decryption(cipherText, decToBinStr(decryptedKey.toString()));
            System.out.println("Decrypted Plaintext: " + Integer.parseInt(plainText, 2));

            // Hashing the plaintext
            Hash hash = new Hash();
            String digest = hash.encryptThisString(Integer.toString(Integer.parseInt(plainText, 2)));
            System.out.println("Message Digest: " + digest);
            BigInteger temp = new BigInteger(digest, 16);
            BigInteger serverDigest = new BigInteger(temp.toString().substring(0, 3));

            // Decrypting the clientSignature
            BigInteger clientDigest = rsa.encryption(clientSignature, client_e, client_n);

            BigInteger signature = rsa.encryption(temp, client_e, client_n);
            System.out.println("Intermediate verification code: " + signature);

            // If the serverDigest and clientDigest matches i.e signature is verified
            if (serverDigest.equals(clientDigest))
                System.out.println("Signature verified");
            else
                System.out.println("Signature not verified ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
