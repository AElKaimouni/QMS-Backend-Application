package com.example.qms.queue.services;

import com.example.qms.queue.Queue;
import com.example.qms.queue.QueueRepository;
import com.example.qms.queue.dto.CreateQueueDTO;
import com.example.qms.queue.dto.QueueDTO;
import com.example.qms.queue.dto.QueueDetailsDTO;
import com.example.qms.queue.dto.UpdateQueueDTO;
import com.example.qms.queue.enums.QueueStatus;
import com.example.qms.queue.exceptions.QueueCounterLimitException;
import com.example.qms.queue.exceptions.QueueNotFoundException;
import com.example.qms.queue.exceptions.QueueOwnershipException;
import com.example.qms.reservation.services.ReservationService;
import com.example.qms.user.config.CustomUserDetails;
import com.example.qms.user.services.UserService;
import com.example.qms.workspace.Workspace;
import com.example.qms.workspace.exceptions.WorkspaceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public  class QueueService implements QueueServiceInterface {
    private final QueueRepository queueRepository;

    @Autowired
    UserService userService;


    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    public String getQueueSecret(String qid) {
        return qid;
    }

    // Encrypt the position using AES-128-ECB
    public String generateToken(int position, UUID qid) throws QueueNotFoundException {
        Queue queue = getMustExistQueue(qid);

        String queueSecret = queue.getSecret().toString();
        try {
            // Ensure the key is exactly 16 bytes (128 bits)
            SecretKeySpec key = new SecretKeySpec(queueSecret.getBytes(StandardCharsets.UTF_8), 0, 16, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // AES with ECB mode and PKCS5Padding
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String positionString = Integer.toString(position);
            byte[] encryptedBytes = cipher.doFinal(positionString.getBytes(StandardCharsets.UTF_8));

            // Return the encrypted data encoded in Base64 to keep it in a readable format
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            // Catch any unexpected exceptions and return null
            e.printStackTrace();
            return null;
        }
    }

    // Decrypt the ticket using AES-128-ECB
    public Integer validateToken(String encryptedTicket, UUID qid) {
        Optional<Queue> queue = getQueue(qid);

        if (queue.isEmpty()) return 0;

        String queueSecret = queue.get().getSecret().toString();
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

    public Optional<Queue> getQueue(UUID queueId) {
        // Implementation for getting a queue
        return queueRepository.findById(queueId);
    }

    public int getQueueLength(UUID queueId) {
        Queue queue = getMustExistQueue(queueId);
        return queue.getLength();
    }
    public int getQueueLengthLastHour(UUID queueId){
        return queueRepository.getLastHourQueueLength(queueId);

    }


    public Queue getMustExistQueue(UUID queueId) throws QueueNotFoundException {
        // Implementation for getting a queue
        Optional<Queue> queue = this.getQueue(queueId);

        if(queue.isEmpty()) throw  new QueueNotFoundException();

        return  queue.get();
    }

    public QueueDTO createQueue(CreateQueueDTO dto, Long userId, Long workspaceId) {
        Queue queue = new Queue(
            dto.getTitle(),
            dto.getDescription(),
            dto.getConfig(),
            workspaceId,
            userId
        );

        queue.setSecret(UUID.randomUUID());
        queue.setCreatedAt(LocalDateTime.now());

        try {
            Queue createdQueue = queueRepository.save(queue);

            return convertToDTO(createdQueue);
        } catch (DataIntegrityViolationException e) {
            throw new WorkspaceNotFoundException();
        }
    }



    public QueueDTO updateQueue(UUID queueId, CreateQueueDTO dto) {
        // Verify that the current user is the owner of the queue
        Queue queue = verifyOwnership(queueId);

        // Update only the fields present in UpdateQueueDTO
        if (dto.getTitle() != null) queue.setTitle(dto.getTitle());
        if (dto.getDescription() != null) queue.setDescription(dto.getDescription());
        if (dto.getConfig() != null) queue.setConfig(dto.getConfig());

        queue.setUpdatedAt(LocalDateTime.now());

        try {
            Queue updatedQueue = queueRepository.save(queue);
            return convertToDTO(updatedQueue);
        } catch (DataIntegrityViolationException e) {
            throw new QueueNotFoundException();
        }
    }

    public Integer reserve(UUID queueId) throws QueueNotFoundException {
        // Implementation for reserving a queue
        Queue queue = getMustExistQueue(queueId);
        queue.setLength(queue.getLength() + 1);

        queueRepository.save(queue);
        return queue.getLength();
    }

    public Queue next(UUID queueId) throws QueueNotFoundException, QueueCounterLimitException {
        // Implementation for moving to the next queue
        Queue queue = getMustExistQueue(queueId);

        if(queue.getCounter() < queue.getLength()) {
            queue.setCounter(queue.getCounter() + 1);
            queueRepository.save(queue);
        } else throw new QueueCounterLimitException(queue);

        return queue;
    }

    public void delete(UUID queueId) throws QueueNotFoundException  {
        // Implementation for deleting a queue
        Queue queue = getMustExistQueue(queueId);
        queue.setStatus(QueueStatus.DELETED);
        queueRepository.save(queue);
    }

    public void start(UUID queueId) throws QueueNotFoundException {
        // Implementation for starting a queue
        Queue queue = getMustExistQueue(queueId);
        queue.setStatus(QueueStatus.ACTIVE);
        queueRepository.save(queue);
    }

    public void paused(UUID queueId) throws QueueNotFoundException {
        // Implementation for stopping a queue
        Queue queue = getMustExistQueue(queueId);
        queue.setStatus(QueueStatus.PAUSED);
        queueRepository.save(queue);
    }

    public void close(UUID queueId) throws QueueNotFoundException {
        // Implementation for closing a queue
        Queue queue = getMustExistQueue(queueId);
        queue.setStatus(QueueStatus.CLOSED);
        queueRepository.save(queue);
    }

    public List<QueueDTO> getQueues(long userId, long workspaceId) {
        // Fetch all reservations for the queue by its ID
        List<Queue> queues = queueRepository.findByUserIdAndWorkspaceId(userId, workspaceId);

        return queueRepository.findByUserIdAndWorkspaceId(userId, workspaceId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public double getAverageServingTime(UUID qid) {
        Optional<Double> estimation = queueRepository.findAverageServingTimeForQueue(qid);
        if(estimation.isPresent()) return estimation.get();

        return 0;
    }

    public Long getUserIdByQueueId(UUID queueId) throws QueueNotFoundException {
        return queueRepository.findUserIdByQueueId(queueId);
    }

    private QueueDTO convertToDTO(Queue queue) {
        QueueDTO dto = new QueueDTO();

        dto.setDescription(queue.getDescription());
        dto.setId(queue.getId());
        dto.setTitle(queue.getTitle());
        dto.setUpdatedAt(queue.getUpdatedAt());
        dto.setUserId(queue.getUserId());
        dto.setWorkspaceId(queue.getWorkspaceId());
        dto.setLength(queue.getLength());
        dto.setCounter(queue.getCounter());
        dto.setCreatedAt(queue.getCreatedAt());
        dto.setStatus(queue.getStatus());

        return dto;
    }


    public Queue verifyOwnership(UUID queueId) {
        // Authenticate the user
        CustomUserDetails userDetails = userService.auth();
        Long userId = userDetails.getId();

        // Get the queue and check if the authenticated user is the owner
        Queue queue = getMustExistQueue(queueId);
        if (!userId.equals(queue.getUserId())) {
            throw new QueueOwnershipException("User is not the owner of the queue");
        }

        return queue;
    }

    public long getQueuesCount(long user_id, QueueStatus status) {
        if(status == null) return queueRepository.countTotalQueuesByUserId(user_id);

        return  queueRepository.countQueuesByUserIdAndStatus(user_id, status);
    }

    public long getQueuesCount(long user_id) {
        return getQueuesCount(user_id, null);
    }

    public QueueDetailsDTO getQueueDetails(UUID queueId) {

        Queue queue = getMustExistQueue(queueId);
        QueueDetailsDTO dto = new QueueDetailsDTO();
        dto.setId(queue.getId());
        dto.setTitle(queue.getTitle());
        dto.setDescription(queue.getDescription());
        Workspace workspace = queue.getWorkspace();
        dto.setWorkspaceName(workspace.getTitle());
        dto.setStatus(queue.getStatus());
        return dto;
    }
}