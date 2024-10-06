package com.example.qms.queue.services;

import com.example.qms.queue.Queue;
import com.example.qms.queue.QueueRepository;
import com.example.qms.queue.dto.CreateQueueRequest;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class QueueService {

    private final QueueRepository queueRepository;

    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

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

    public Queue getQueue(UUID queueId) {
        // Implementation for getting a queue
        return queueRepository.findById(queueId)
                .orElseThrow(() -> new IllegalStateException("Queue not found"));
    }

    public String createQueue(
            String title,
            int length,
            Queue.QueueStatus status
    ) {
        // Implementation for creating a queue
        Queue queue = new Queue(title, length, status);
        queue.setSecret(UUID.randomUUID());
        queue.setCreatedAt(LocalDateTime.now());
        queueRepository.save(queue);
        return queue.getId().toString();
    }

    public void reserve(UUID queueId) {
        // Implementation for reserving a queue
        Queue queue = getQueue(queueId);
        queue.setLength(queue.getLength() + 1);
        queueRepository.save(queue);
    }

    public void next(UUID queueId) {
        // Implementation for moving to the next queue
        Queue queue = getQueue(queueId);
        queue.setCounter(queue.getCounter() + 1);
        queueRepository.save(queue);
    }

    public void delete(UUID queueId) {
        // Implementation for deleting a queue
        Queue queue = getQueue(queueId);
        queue.setStatus(Queue.QueueStatus.DELETED);
        queueRepository.save(queue);
    }

    public void start(UUID queueId) {
        // Implementation for starting a queue
        Queue queue = getQueue(queueId);
        queue.setStatus(Queue.QueueStatus.ACTIVE);
        queueRepository.save(queue);
    }

    public void paused(UUID queueId) {
        // Implementation for stopping a queue
        Queue queue = getQueue(queueId);
        queue.setStatus(Queue.QueueStatus.PAUSED);
        queueRepository.save(queue);
    }

    public void close(UUID queueId) {
        // Implementation for closing a queue
        Queue queue = getQueue(queueId);
        queue.setStatus(Queue.QueueStatus.CLOSED);
        queueRepository.save(queue);
    }

    public boolean validateToken() {
        // Implementation for validating a token
        return false;
    }

    public String generateToken() {
        // Implementation for generating a token
        return null;
    }
}