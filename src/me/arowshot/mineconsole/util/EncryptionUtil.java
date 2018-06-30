package me.arowshot.mineconsole.util;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class EncryptionUtil {
    public static Cipher createCipher(int opmode, String algorithm, Key key) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(opmode, key);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        
        System.err.println("Cipher creation failed!");
        return null;
    }
    
    public static Cipher createCipher(int opmode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(opmode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch(GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static byte[] cipherOperation(int opmode, Key key, byte[] toOp) {
        try {
            return createCipher(opmode, key.getAlgorithm(), key).doFinal(toOp);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        
        System.err.println("Cipher data failed!");
        return null;
    }
    
    public static PublicKey decodePublicKey(byte[] key) {
        try {
            X509EncodedKeySpec keyspec = new X509EncodedKeySpec(key);
            KeyFactory var2 = KeyFactory.getInstance("RSA");
            return var2.generatePublic(keyspec);
        }
        catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        System.err.println("Public key reconstitute failed!");
        return null;
    }
    
    public static SecretKey createNewSharedKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return keyGen.generateKey();
        }
        catch (NoSuchAlgorithmException var1) {
            throw new Error(var1);
        }
    }
    
    public static byte[] digestOperation(String alg, byte[] ... toHash) {
        try {
            MessageDigest m = MessageDigest.getInstance(alg);
            byte[][] bytes = toHash;
            int len = toHash.length;

            for (int i = 0; i < len; ++i) {
                byte[] b = bytes[i];
                m.update(b);
            }

            return m.digest();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
            return null;
        }
    }
}
