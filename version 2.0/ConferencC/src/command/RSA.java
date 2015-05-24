package command;

import java.math.BigInteger;

public class RSA {
private BigInteger n, d, e;

/** Create an instance that can encrypt using someone else's public key. */
public RSA(BigInteger newn, BigInteger newe) {
  n = newn;
  e = newe;
}

/** Create an instance that can both encrypt and decrypt. */
public RSA(int bits) {

  //BigInteger p = new BigInteger(bitlen / 2, 100, r);
  //BigInteger q = new BigInteger(bitlen / 2, 100, r);
  BigInteger p = new BigInteger("11491966412810690412162769578898061749933063956953096747120344699418816095751731331872042072544178740889487854380011183506777899582646542960888396429932343");
  BigInteger q = new BigInteger("7177388289359746616600453815595370177727956480142080331832615398393810388348788034027369223058763207743982230460494172331961174762356564409234613244273869");
  
  n = p.multiply(q);
  BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q
      .subtract(BigInteger.ONE));
  e = new BigInteger("3");
  while (m.gcd(e).intValue() > 1) {
    e = e.add(new BigInteger("2"));
  }
  d = e.modInverse(m);
}

/** Encrypt the given plaintext message. */
public synchronized String encrypt(String message) {
  return (new BigInteger(message.getBytes())).modPow(e, n).toString();
}

/** Encrypt the given plaintext message. */
public synchronized BigInteger encrypt(BigInteger message) {
  return message.modPow(e, n);
}

/** Decrypt the given ciphertext message. */
public synchronized String decrypt(String message) {
  return new String((new BigInteger(message)).modPow(d, n).toByteArray());
}

/** Decrypt the given ciphertext message. */
public synchronized BigInteger decrypt(BigInteger message) {
  return message.modPow(d, n);
}

/** Return the modulus. */
public synchronized BigInteger getN() {
  return n;
}

/** Return the public key. */
public synchronized BigInteger getE() {
  return e;
}

/**public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
	SecureRandom r = new SecureRandom();
	BigInteger p = new BigInteger(bitlen / 2, 100, r);
	BigInteger q = new BigInteger(bitlen / 2, 100, r);
	System.out.println("Plaintext: " + p);
	System.out.println("Plaintext: " + q);
}*/
}

 
  