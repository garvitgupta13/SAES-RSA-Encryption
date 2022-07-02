# Simplified AES with RSA Encryption

```
Name:    GARVIT GUPTA
Roll No: 2019060
```

## Software requirement
* Make sure to install any jdk(prefer: 14) to run these java files
  
## Steps to run file
* Open 2 terminals simultaneously side by side. Run client related command in one and server for other.
* Compile the java files:
  ```
  javac Server.java
  javac Client.java
  ```
* <b>NOTE: </b>Make sure to run the server file first and after that the client file.
* Run Server file `java Server`
* Run   Client file `java Client`
* Pass the parameters to server file
* Pass the message, secret key and public key parameters to client file.

## AES.java
This files contains functions to encrypt the plaintext using key and decrypt the ciphertext back to plain text.
Following is the decription of each function used in this file: 

* <b>decToBinary: </b>Converts a interger to 4bit binary string
* <b>multiply: </b>Multiplies 2 integer using bitwise multiplication and giving a output integer of 4bit integer
* <b>xor: </b>Performs xor openration over 2 strings and returns the output string.
* <b>nibbleSubsitution: </b>We break the 16bit binary string to 4nibble(each of 4 bit) and pass them through s-box and concatenate them all to genrate the result string. This is used while performing encrytion operation.
* <b>invNibbleSubsitution: </b>We break the 16bit binary string to 4nibble(each of 4 bit) and pass them through inverse s-box and concatenate them all to genrate the result string. This is used while performing decrytion operation.
* <b>shiftRows: </b>Interchanges the second and 4th nibble.
* <b>rorateNibble: </b>Interchanges the first and second half of 8bit string. Used while genrating the round keys.
* <b>mixColumns: </b>Performs mix column operation over string s. It is converted to 2X2 matrix multiplied with
  [ 1 4]
  [ 4 1]. The result is converted back to binary string and concatenated to 16 bit binary string.
  This is used while performing encrytion operation.
* <b>invMixColumns: </b>Performs inverse mix column operation over string s. It is converted to 2X2 matrix multiplied with
  [ 9 2]
  [ 2 9]. The result is converted back to binary string and concatenated to 16 bit binary string.
  This is used while performing decrytion operation.
* <b>keyGenrator: </b>Genrates round keys for encrytion and decryption process. It returns an string array of 3 round keys for round one, two and three respectively.
* <b>encryption: </b>It receives plaintext and key as input. They are binary string of 16 length. We genrate round keys for further processing. For round1 the plaintext is xorred with round1 key and after that we pass them through nibbleSubsition function to hash the string wth s-box values. After thar shift row operation is performed which interchanges second and fourth nibble. The the following string is passed to mixcloumn function. These steps are repeated again except for mixColumn for round2. The string receives after round2 is xorred with round3 key to give the 16bit binary string of cipher text.
* <b>decryption: </b>t receives ciphertext and key as input. They are binary string of 16 length. We genrate round keys for further processing. For round1 the ciphertext is xorred with round3 key and after that we passed to shift row operation is performed which interchanges second and fourth nibble. The processed string is now passed through inverse nibble subsitution and then to inverse mix column and then xorred with round2 key. For round2 the string is passed again to shiftrow function and then to inverse nibble subsitution function. To get final plaintext we xor this string with round1 key.

## RSA.java
This files contains functions to encrypt the plaintext using RSA algorithm and genrate the public and private key for RSA algorithm.
Following is the decription of each function used in this file: 

* <b>encryption: </b>It takes message and keys as input. Make sure the input data type is of BigInteger and message value is less than n. It returns the encrypted message. This same function can be used to decrypt a ciphertext text if private keys are passed.
* <b>key_genrator: </b>It takes public key parameters such as p,q and e. Make sure p and q are prime numbers and gcd(e,(p-1)*(q-1)) is 1. It returns an array containing public and private keys.

## Hash.java
This files contains functions to hash a string to 32 bit hexadecimal string.
Following is the decription of each function used in this file: 
* <b>encryptThisString: </b>This function uses MD2 algorithm to hash a string to 32bit hexadecimal string.

## Client.java
This file helps us to execute Client side commands.
Following is the decription of each function used in this file: 

* <b>main: </b>We first initialize a socket object at port 8080. Then we ask client to enter message, secret key and public key parameters p,q and e. Make sure that p and q are prime numbers and gcd of e and (p-1)*(q-1) is 1 else the program will be terminated. Then we receive server public keys from server. The secret key is then encrypted using RSA algorithm with server's public key. Message is encrypted using AES algorithm and secret key. The message is hashed to genrate digest. The digest is very long integer which cannot be passed through RSA so it is filtered using filterDigest function. The filtered digest and is encrypted using RSA algorithm with clients private key. These data is then send to server.
* <b>decToBinStr: </b>Converts a decimal string to 16 bit binary string
* <b>isPrime: </b>Checks if a number is prime or not
* <b>gcd: </b>Calculates gcd of integers using eucledian algorithm
* <b>filterDigest: </b>Returns the first three intergers of digest

## Server.java
This file helps us to execute Server side commands.
Following is the decription of each function used in this file: 

* <b>main: </b>We first initialize a socket object at port 8080. Then we ask client to enter public key parameters p,q and e. Make sure that p and q are prime numbers and gcd of e and (p-1)*(q-1) is 1 else the program will be terminated. Then we genrate the server side public and private key using rsa algorithm. Then we receive data from client. The encrypted secret key received from client is decrypted using server's private key. The cipher text received is now decrypted to plain text using AES algorithm. Now the digest is genrated by hashing the plaintext. The client signature recieved from client is now decrypted using rsa algorithm with client's public key. If the decrypted client signature and first 3 integers matches we can say that signature is verified.
* <b>decToBinStr: </b>Converts a decimal string to 16 bit binary string
* <b>isPrime: </b>Checks if a number is prime or not
* <b>gcd: </b>Calculates gcd of integers using eucledian algorithm