import java.util.HashMap;

public class AES {
    //Convert a decimal to 4bit binary
    private static String decToBinary(int num){
        String res=Integer.toBinaryString(num);
        while(res.length()<4){
            res="0"+res;
        }
        return res;
    }

    //Multiply 2 integers to get
    private static int multiply(int n1,int n2){
        int num=0;
        while( n2>0 )
        {
            if ((n2 & 0b1)>0)
                num ^= n1;
            n1 <<= 1;
            if ((n1 & 0b10000)>0)
                n1 ^= 0b11;
            n2 >>= 1;
        }
        return (num & 0b1111);
    }

    //XOR 2 strings
    private static String xor(String s1, String s2,int n){
        String res="";
        for(int i=0;i<n;i++)
        {
            if(s1.charAt(i)==s2.charAt(i))
                res+="0";
            else
                res+="1";
        }
        return res;
    }

    //Performs nibble subsitution
    private static String nibbleSubsitution(String nibble,int n){
        HashMap<String, String> s_box = new HashMap<>();
        s_box.put("0000", "1001");
        s_box.put("0001", "0100");
        s_box.put("0010", "1010");
        s_box.put("0011", "1011");
        s_box.put("0100", "1101");
        s_box.put("0101", "0001");
        s_box.put("0110", "1000");
        s_box.put("0111", "0101");
        s_box.put("1000", "0110");
        s_box.put("1001", "0010");
        s_box.put("1010", "0000");
        s_box.put("1011", "0011");
        s_box.put("1100", "1100");
        s_box.put("1101", "1110");
        s_box.put("1110", "1111");
        s_box.put("1111", "0111");
        String res = "";
        for(int i=0;i<n;i++) {
            res+=s_box.get(nibble.substring(i * 4, i * 4 + 4));
        }
        return res;
    }

    //Performs inverse nibble subsitution
    public static String invNibbleSubsitution(String nibble){
        HashMap<String, String> s_box = new HashMap<>();
        String res = "";
        s_box.put("1001","0000");
        s_box.put("0100","0001");
        s_box.put("1010","0010");
        s_box.put("1011","0011");
        s_box.put("1101","0100");
        s_box.put("0001","0101");
        s_box.put("1000","0110");
        s_box.put("0101","0111");
        s_box.put("0110","1000");
        s_box.put("0010","1001");
        s_box.put("0000","1010");
        s_box.put("0011","1011");
        s_box.put("1100","1100");
        s_box.put("1110","1101");
        s_box.put("1111","1110");
        s_box.put("0111","1111");
        for(int i=0;i<4;i++) {
            res+=s_box.get(nibble.substring(i * 4, i * 4 + 4));
        }
        return res;
    }

    //Performs shift row operation over string s and returns the modified string
    private static String shiftRows(String s){
        return s.substring(0,4)+s.substring(12)+s.substring(8,12)+s.substring(4,8);
    }

    //Interchanges the first and second half of 8bit string
    public static String rotateNibble(String s){
        return s.substring(4)+s.substring(0,4);
    }

    //Performs mix column operation over string s
    //Each 4bit part of binary string is converted to integer and
    //passed to multiply function and then these decimal is converted back to
    //binary string and joined together
    private static String mixColumns(String s){
        int nibble1=Integer.parseInt(s.substring(0,4),2);
        int nibble2=Integer.parseInt(s.substring(4,8),2);
        int nibble3=Integer.parseInt(s.substring(8,12),2);
        int nibble4=Integer.parseInt(s.substring(12),2);
        int part1=nibble1^multiply(4,nibble2);
        int part2=nibble2^multiply(4,nibble1);
        int part3=nibble3^multiply(4,nibble4);
        int part4=nibble4^multiply(4,nibble3);
        return decToBinary(part1)+decToBinary(part2)+decToBinary(part3)+decToBinary(part4);
    }

    //Performs inverse mix column operation over string s
    //Each 4bit part of binary string is converted to integer and
    //passed to multiply function and then these decimal is converted back to
    //binary string and joined together
    private static String invMixColumns(String s){
        int nibble1=Integer.parseInt(s.substring(0,4),2);
        int nibble3=Integer.parseInt(s.substring(4,8),2);
        int nibble2=Integer.parseInt(s.substring(8,12),2);
        int nibble4=Integer.parseInt(s.substring(12),2);
        int part1=multiply(9,nibble1)^multiply(2,nibble3);
        int part2=multiply(9,nibble2)^multiply(2,nibble4);
        int part3=multiply(2,nibble1)^multiply(9,nibble3);
        int part4=multiply(2,nibble2)^multiply(9,nibble4);
        return decToBinary(part1)+decToBinary(part3)+decToBinary(part2)+decToBinary(part4);
    }

    //This function takes key string as input and returns a string array of round keys of round 1,2 and 3.
    private static String[] keyGenrator(String key){
        String w0=key.substring(0,8), w1=key.substring(8);
        String w2=xor(w0,"10000000",8);
        w2=xor(w2,nibbleSubsitution(rotateNibble(w1),2),8);
        String w3=xor(w2,w1,8);
        String w4=xor(w2,"00110000",8);
        w4=xor(w4,nibbleSubsitution(rotateNibble(w3),2),8);
        String w5=xor(w4,w3,8);
        String key2=w2+w3;
        String key3=w4+w5;
        return new String[]{key, key2, key3};
    }

    //Receives plainText and secret key as input and return the cipherText
    public static String encryption(String plainText, String key){
        //Genrate the round keys for each round
        String[] keys=keyGenrator(key);
        //Round-1
        //XOR the plaintext and round1 key
        String xorred1=xor(plainText,key,16);
        System.out.println("After Pre-round transformation: "+xorred1);
        System.out.println("Round key K0: "+keys[0]);

        //Perform nibble subsitution over the xorred string
        String afterNibbleSubs1=nibbleSubsitution(xorred1,4);
        System.out.println("After Round 1 Substitute nibbles: "+afterNibbleSubs1);

        //Perform shift row function to string received after first nibble subsitution
        String afterShiftRow1=shiftRows(afterNibbleSubs1);
        System.out.println("After Round 1 Shift rows: "+afterShiftRow1);

        //Performing mix cloumn operation over the shift rows string
        String afterMixColumn=mixColumns(afterShiftRow1);
        System.out.println("After Round 1 Mix columns: "+afterMixColumn);

        //Round-2
        //XOR the string received after mixcloumn operation with round2 key
        String xorred2=xor(afterMixColumn,keys[1],16);
        System.out.println("After Round 1 Add round key: "+xorred2);
        System.out.println("Round key K1: "+keys[1]);

        //Perform nibble subsitution over the round 2 xorred string
        String afterNibbleSubs2=nibbleSubsitution(xorred2,4);
        System.out.println("After Round 2 Substitute nibbles: "+afterNibbleSubs2);

        //Pass the string received after round2 subsitution to shift row function to exchage the nibbles
        String afterShiftRow2=shiftRows(afterNibbleSubs2);
        System.out.println("After Round 2 Shift rows: "+afterShiftRow2);

        //XOR the processed string with round3 key to get cipher text
        String xorred3=xor(afterShiftRow2,keys[2],16);
        System.out.println("After Round 2 Add round key: "+xorred3);
        System.out.println("Round key K2: "+keys[2]);
        return xorred3;
    }

    //Receives cipherText and secret key as input and return the plainText
    public static String decryption(String ciphertext, String key){
        //Genrate the round keys for each round
        String[] keys=keyGenrator(key);

        //Round-1
        //XOR the ciphertext and round3 key
        String xorred1=xor(ciphertext,keys[2],16);
        System.out.println("After Pre-round transformation: "+xorred1);
        System.out.println("Round key K2: "+keys[2]);

        //Perform the shift row operation over xorred string
        String afterInvShiftRow1=shiftRows(xorred1);
        System.out.println("After Round 1 InvShift rows: "+afterInvShiftRow1);

        //Perform inverse nibble subsitution over processed string
        String afterInvNibbleSubs1=invNibbleSubsitution(afterInvShiftRow1);
        System.out.println("After Round 1 InvSubstitute nibbles: "+afterInvNibbleSubs1);

        //Perform XOR operation with inverted string and round2 key
        String xorred2=xor(afterInvNibbleSubs1,keys[1],16);
        System.out.println("After Round 1 InvAdd round key: "+xorred2);
        System.out.println("Round key K1: "+keys[1]);

        //Perform the inverse mix cloumn operation over the xorred string
        String afterInvMixColumn=invMixColumns(xorred2);
        System.out.println("After Round 1 Mix columns: "+afterInvMixColumn);

        //Round2
        //Pass the proceesed string to shiftRows function to interchange the 2nd and 4th nibble
        String afterShiftRow2=shiftRows(afterInvMixColumn);
        System.out.println("After Round 2 InvShift rows: "+afterShiftRow2);

        //Perform inverse nibble subsitution
        String afterNibbleSubs2=invNibbleSubsitution(afterShiftRow2);
        System.out.println("After Round 2 InvSubstitute nibbles "+afterNibbleSubs2);

        //Perform xor of processed string with round1 key to get plain text
        String xorred3=xor(afterNibbleSubs2,keys[0],16);
        System.out.println("After Round 2 Add round key: "+xorred3);
        System.out.println("Round key K0: "+keys[0]);
        return xorred3;
    }
}
