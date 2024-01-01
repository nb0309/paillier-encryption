import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;

public class PaillierClient {

    public static class Paillier {

        private final int keyLength;
        private final BigInteger n;
        private final BigInteger nSquared;
        private final BigInteger g;
        private final BigInteger lambda;
        private final BigInteger mu;

        public Paillier(int keyLength) {
            this.keyLength = keyLength;
            SecureRandom random = new SecureRandom();

            // Step 1: Generate two large prime numbers, p and q
            BigInteger p = BigInteger.probablePrime(keyLength / 2, random);
            BigInteger q = BigInteger.probablePrime(keyLength / 2, random);

            // Step 2: Compute n = p * q and nSquared = n^2
            n = p.multiply(q);
            nSquared = n.pow(2);

            // Step 3: Choose a random integer g such that gcd(L(g^lambda mod n^2), n) = 1
            g = BigInteger.valueOf(2); // You may need to find a suitable g in a real implementation

            // Step 4: Compute lambda = lcm(p-1, q-1)
            lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));

            // Step 5: Compute mu = L(g^lambda mod n^2)^-1 mod n
            mu = L(g.modPow(lambda, nSquared)).modInverse(n);
        }

        public BigInteger encrypt(BigInteger plaintext) {
            // Step 1: Choose a random r such that 0 < r < n
            SecureRandom random = new SecureRandom();
            BigInteger r = new BigInteger(keyLength, random);

            // Step 2: Compute ciphertext as c = (g^plaintext mod n^2) * (r^n mod n^2) mod n^2
            BigInteger c1 = g.modPow(plaintext, nSquared).mod(nSquared);
            BigInteger c2 = r.modPow(n, nSquared).mod(nSquared);
            BigInteger ciphertext = c1.multiply(c2).mod(nSquared);

            return ciphertext;
        }

        public BigInteger decrypt(BigInteger ciphertext) {
            // Step 1: Compute plaintext as m = L(c^lambda mod n^2) * mu mod n
            BigInteger m = L(ciphertext.modPow(lambda, nSquared)).multiply(mu).mod(n);

            return m;
        }

        public BigInteger addCiphertexts(BigInteger ciphertext1, BigInteger ciphertext2) {
            // Homomorphic addition: c3 = c1 * c2 mod n^2
            return ciphertext1.multiply(ciphertext2).mod(nSquared);
        }

        public BigInteger multiplyCiphertextByConstant(BigInteger ciphertext, BigInteger constant) {
            // Homomorphic multiplication: c3 = c1^constant mod n^2
            return ciphertext.modPow(constant, nSquared);
        }

        private BigInteger lcm(BigInteger a, BigInteger b) {
            return a.multiply(b).divide(a.gcd(b));
        }

        private BigInteger L(BigInteger x) {
            return x.subtract(BigInteger.ONE).divide(n);
        }
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            System.out.println("Connected to the server.");

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            // Example plaintext
            BigInteger plaintext = new BigInteger("10");
            System.out.println("Plaintext: " + plaintext);

            // Encrypt the plaintext
            Paillier paillier = new Paillier(1024);
            BigInteger encryptedMessage = paillier.encrypt(plaintext);
            System.out.println("Encrypted Message: " + encryptedMessage);

            // Send the encrypted message to the server
            oos.writeObject(encryptedMessage);
            System.out.println("Sent encrypted message to the server.");

            // Flush the ObjectOutputStream to ensure the data is sent immediately
            oos.flush();

            // Receive the result from the server
            BigInteger result = (BigInteger) ois.readObject();
            System.out.println("Received result from the server: " + result);

            // Decrypt the result
            BigInteger decryptedResult = paillier.decrypt(result);
            System.out.println("Decrypted Result: " + decryptedResult);

            // Close the streams and the socket
            ois.close();
            oos.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
