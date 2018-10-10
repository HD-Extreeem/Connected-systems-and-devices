import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Hadi Deknache && Yurdaer Dalkic
 * 
 */
public class RSA {
	// private BigInteger d;
	int p, q, e, n, z, d = 0;

	/**
	 * This constructor generate RSA private and public keys. An do-while loop
	 * checks if generated keys are correct.
	 */
	public RSA() {

		ArrayList<Integer> primes = getPrime(100, 200); // Get primes
		Random random = new Random();
		int test = 123;
		do {
			p = primes.get(random.nextInt(primes.size())); // Select p
			q = primes.get(random.nextInt(primes.size())); // Select q

			while (q == p) { // Check if p and q are equals. If they are, chose
								// a new p
				p = primes.get(random.nextInt(primes.size()));
			}

			n = p * q; // Calculate n

			z = (q - 1) * (p - 1); // Calculate z

			for (e = 2; e < n; e++) { // Calculate e and z
				if ((genE(e, z)) == 1) {
					break;
				}

			}

			while (true) { // Calculate d
				if (((e * d) % z) == 1) {
					break;
				}
				d++;
			}
			System.out.println("RSA TEST");
			System.out.println("RSA enrypt key :"
					+ RSAEncrypt(test, e, n).intValue());
			System.out
					.println("RSA decrypt key :"
							+ (RSADecrypt(RSAEncrypt(test, e, n).intValue())
									.intValue()));
		} while ((RSADecrypt(RSAEncrypt(test, e, n).intValue()).intValue()) != test); // Do
																						// a
																						// test
																						// that
																						// checks
																						// if
																						// it
																						// is
																						// possible
																						// ecrypt
																						// than
																						// decrypt
																						// a
																						// test
																						// value
																						// with
																						// calculated
																						// RSA
																						// keys.

		System.out.println("p = " + p);
		System.out.println("q = " + q);
		System.out.println("e = " + e);
		System.out.println("n = " + n);
		System.out.println("z = " + z);
		System.out.println("d = " + d);
	}

	/**
	 * This method decrypts an encrypted value with given RSA private key
	 * 
	 * @param key
	 * @return
	 */
	public BigInteger RSADecrypt(int key) {
		return new BigDecimal(key).toBigInteger().modPow(
				new BigDecimal(d).toBigInteger(),
				new BigDecimal(n).toBigInteger());
	}

	/**
	 * This method encrypts a value with given RSA public key
	 * 
	 * @param key
	 * @param e
	 * @param n
	 * @return
	 */
	private BigInteger RSAEncrypt(int key, int e, int n) {
		return new BigDecimal(key).toBigInteger().modPow(
				new BigDecimal(e).toBigInteger(),
				new BigDecimal(n).toBigInteger());
	}

	/**
	 * This method generates RSA public key e
	 * 
	 * @param e
	 * @param z
	 * @return
	 */
	private int genE(int e, int z) {
		if (e == 0) {
			return z;
		} else {
			return genE(z % e, e);
		}
	}

	/**
	 * This method generates prime numbers within given interval.
	 * 
	 * @param start
	 * @param end
	 * @return an array which filled with prime numbers
	 */
	private ArrayList<Integer> getPrime(int start, int end) {
		ArrayList<Integer> primeArr = new ArrayList<Integer>();
		for (int i = start; i < end; i++) {
			for (int j = 2; j < i; j++) {
				if (i % j == 0) {
					break;
				}
				if (j + 1 == i) {
					primeArr.add(i);
				}

			}
		}
		return primeArr;

	}

	/**
	 * This is a getter for instants variable e
	 * 
	 * @return
	 */
	public int getE() {
		return e;
	}

	/**
	 * This is a getter for instants variable n
	 * 
	 * @return
	 */
	public int getN() {
		return n;
	}

}
