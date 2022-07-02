import java.math.BigInteger;

public class RSA {
    //Encrypt a message
    public static BigInteger encryption (BigInteger message,BigInteger e,BigInteger n){
        return message.modPow(e,n);
    }

    //Generate the public keys for RSA algorithm
    public static int[] key_genrator(int p,int q,int e){
        int n=p*q;
        int phi_n=(p-1)*(q-1);
        int d;
        for(d=1;d<phi_n;d++){
            if(((e%phi_n)*(d%phi_n))%phi_n==1){
                break;
            }
        }
        int[] res={e,d,n};
        return res;
    }
}
