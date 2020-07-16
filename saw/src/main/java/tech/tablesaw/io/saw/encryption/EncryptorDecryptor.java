package tech.tablesaw.io.saw.encryption;

/** */
public interface EncryptorDecryptor {

  byte[] encrypt(byte[] plainText);

  byte[] decrypt(byte[] cipherText);
}
