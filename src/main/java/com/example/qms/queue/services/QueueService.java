package com.example.qms.queue.services;


import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class QueueService {

    public String getQueueSecret(String qid) {
        return qid;
    }

    // Encrypt the position using AES-128-ECB
    public String generateToken(int position, String queueSecret) throws Exception {
        // Ensure the key is exactly 16 bytes (128 bits)
        SecretKeySpec key = new SecretKeySpec(queueSecret.getBytes(StandardCharsets.UTF_8), 0, 16, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // AES with ECB mode and PKCS5Padding
        cipher.init(Cipher.ENCRYPT_MODE, key);

        String positionString = Integer.toString(position);
        byte[] encryptedBytes = cipher.doFinal(positionString.getBytes(StandardCharsets.UTF_8));

        // Return the encrypted data encoded in Base64 to keep it in a readable format
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt the ticket using AES-128-ECB
    public Integer validateToken(String encryptedTicket, String queueSecret) {
        try {
            // Ensure the key is exactly 16 bytes (128 bits)
            SecretKeySpec key = new SecretKeySpec(queueSecret.getBytes(StandardCharsets.UTF_8), 0, 16, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedTicket);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            // Return the decrypted position as an integer
            return Integer.parseInt(new String(decryptedBytes, StandardCharsets.UTF_8));

        } catch (IllegalBlockSizeException | BadPaddingException | NumberFormatException e) {
            // If decryption fails or format is wrong, return null
            return 0;
        } catch (Exception e) {
            // Catch any other unexpected exceptions and return null
            e.printStackTrace();
            return 0;
        }
    }
}
