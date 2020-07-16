package tech.tablesaw.io.saw.encryption;

import org.jasypt.encryption.pbe.PooledPBEByteEncryptor;

/**
 * Encrypts and decrypts text in the form of byte arrays. It delegates to a pooled encryptor for
 * parallelization
 */
class StandardTextEncryptor implements EncryptorDecryptor {

  private final PooledPBEByteEncryptor textEncryptor = new PooledPBEByteEncryptor();

  StandardTextEncryptor(String password) {
    textEncryptor.setPassword(password);
    textEncryptor.setPoolSize(3);
  }

  @Override
  public byte[] encrypt(byte[] plainText) {
    return textEncryptor.encrypt(plainText);
  }

  @Override
  public byte[] decrypt(byte[] cipherText) {
    return textEncryptor.decrypt(cipherText);
  }
}
