import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class RSA {
	// private BigInteger d;
	int p, q, e, n, z, d = 0;

	public RSA() {
		ArrayList<Integer> primes = getPrime(100, 150);
		Random random = new Random();

		p = primes.get(random.nextInt(primes.size()));
		q = primes.get(random.nextInt(primes.size()));

		while (q == p) {
			p = primes.get(random.nextInt(primes.size()));

		}

		n = p * q;

		z = (q - 1) * (p - 1);

		for (e = 2; e < n; e++) {
			if ((genE(e, z)) == 1) {
				break;
			}

		}

		while (true) {
			if (((e * d) % z) == 1) {
				break;
			}
			d++;
		}
		/*p  = 281;
		q  = 173;
		e  = 3;
		n  = 48613;
		z  = 48160;
		d  = 32107;*/
		System.out.println("p = " + p);
		System.out.println("q = " + q);
		System.out.println("e = " + e);
		System.out.println("n = " + n);
		System.out.println("z = " + z);
		System.out.println("d = " + d);
	}

	public BigInteger RSADecrypt(int key) {
		return new BigDecimal(key).toBigInteger().modPow(
				new BigDecimal(d).toBigInteger(),
				new BigDecimal(n).toBigInteger());
	}

	private BigInteger RSAEncrypt(int key, int e, int n) {
		return new BigDecimal(key).toBigInteger().modPow(
				new BigDecimal(e).toBigInteger(),
				new BigDecimal(n).toBigInteger());
	}

	private int genE(int e, int z) {
		if (e == 0) {
			return z;
		} else {
			return genE(z % e, e);
		}
	}

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

	public int getE() {
		return e;
	}

	public int getN() {
		return n;
	}

}
