package com.example.qms;
import com.example.qms.analytics.queueSummary.QueueSummary;
import com.example.qms.analytics.queueSummary.QueueSummaryRepository;
import com.example.qms.queue.Queue;
import com.example.qms.queue.QueueRepository;
import com.example.qms.queue.dto.QueueDTO;
import com.example.qms.queue.services.QueueService;
import com.example.qms.queue.dto.CreateQueueDTO;
import com.example.qms.user.User;
import com.example.qms.user.UserRepository;
import com.example.qms.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;
import com.example.qms.workspace.Workspace;
import com.example.qms.workspace.dto.WorkspaceCreateDTO;
import com.example.qms.workspace.dto.WorkspaceDTO;
import com.example.qms.workspace.services.WorkspaceService;
import com.example.qms.workspace.exceptions.MaxWorkspacesLimitException;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private QueueSummaryRepository queueSummaryRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Long USER_ID = 3L;
    private static final int NUM_QUEUES = 5;
    private static final int NUM_SUMMARIES_PER_QUEUE = 10;

    @Override
    public void run(String... args) {
        try {
            // Verify User exists
            User user = userRepository.getUserByid(USER_ID);
            if (user == null) {
                System.err.println("User with ID " + USER_ID + " not found.");
                return;
            }

            // Step 1: Create a Workspace for the User
            WorkspaceCreateDTO workspaceCreateDTO = new WorkspaceCreateDTO();
            workspaceCreateDTO.setTitle("Demo Workspace");
            workspaceCreateDTO.setBusinessName("Demo Business");
            workspaceCreateDTO.setBusinessIndustry("Technology");
            workspaceCreateDTO.setContactEmail("demo@example.com");
            workspaceCreateDTO.setContactPhone("123-456-7890");

            WorkspaceDTO workspaceDTO = workspaceService.createWorkspace(workspaceCreateDTO, USER_ID);
            Long workspaceId = workspaceDTO.getId();
            Workspace workspace = workspaceService.getWorkspace(workspaceId);

            // Step 2: Create Queues for the Workspace
            Random random = new Random();
            for (int i = 0; i < NUM_QUEUES; i++) {
                // Create DTO for each queue
                CreateQueueDTO createQueueDTO = new CreateQueueDTO();
                createQueueDTO.setTitle("Queue " + (i + 1));
                createQueueDTO.setDescription("Description for Queue " + (i + 1));
                createQueueDTO.setConfig(null); // Config is optional

                // Create and save Queue
                QueueDTO queueDTO = queueService.createQueue(createQueueDTO, USER_ID, workspaceId);

                // Retrieve the Queue entity by ID to use in QueueSummary
                Optional<Queue> queueEntityOptional = queueService.getQueue(queueDTO.getId());
                if (queueEntityOptional.isPresent()) {
                    Queue queueEntity = queueEntityOptional.get();

                    // Step 3: Create QueueSummary entries for each Queue
                    for (int j = 0; j < NUM_SUMMARIES_PER_QUEUE; j++) {
                        QueueSummary summary = new QueueSummary();
                        summary.setQueue(queueEntity);
                        summary.setUser(user);
                        summary.setWorkspace(workspace);
                        summary.setDate(LocalDate.now().minusDays(j)); // Daily summaries up to NUM_SUMMARIES_PER_QUEUE days ago
                        summary.setVisitorsCount(random.nextInt(100) + 1); // Random visitors count between 1 and 100
                        summary.setAverageWaitTime(5 + (10 * random.nextDouble())); // Random wait time between 5 and 15 minutes
                        summary.setAverageServeTime(3 + (7 * random.nextDouble())); // Random serve time between 3 and 10 minutes
                        summary.setTotalServed(random.nextInt(80) + 1); // Random total served count between 1 and 80

                        queueSummaryRepository.save(summary);
                    }
                }
            }

            System.out.println("Sample data initialization completed.");

        } catch (MaxWorkspacesLimitException e) {
            System.err.println("Max workspaces limit reached for user " + USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
