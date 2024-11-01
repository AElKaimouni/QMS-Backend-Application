package com.example.qms.workspace;

import com.example.qms.user.config.CustomUserDetails;
import com.example.qms.user.services.UserService;
import com.example.qms.workspace.dto.WorkspaceCreateDTO;
import com.example.qms.workspace.dto.WorkspaceDTO;
import com.example.qms.workspace.exceptions.MaxWorkspacesLimitException;
import com.example.qms.workspace.exceptions.MinWorkspacesLimitException;
import com.example.qms.workspace.exceptions.WorkspaceNotFoundException;
import com.example.qms.workspace.services.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {

    @Autowired
    private UserService userService;

    @Autowired
    private WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<?> createWorkspace(@Valid @RequestBody WorkspaceCreateDTO workspaceCreateDTO) {
        try {
            CustomUserDetails userDetails = userService.auth();

            WorkspaceDTO createdWorkspace = workspaceService.createWorkspace(workspaceCreateDTO, userDetails.getId());
            return ResponseEntity.ok(createdWorkspace);
        } catch(MaxWorkspacesLimitException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceDTO> getWorkspaceById(@PathVariable long id) {
        try {
            CustomUserDetails userDetails = userService.auth();
            Workspace workspace = workspaceService.getWorkspace(id);

            if(workspace.getUserId() != userDetails.getId())
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            return ResponseEntity.ok(workspaceService.convertToDTO(workspace));
        } catch(WorkspaceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{user_id}/all")
    public ResponseEntity<List<WorkspaceDTO>> getAllWorkspaces(@PathVariable("user_id") long userId) {
        List<WorkspaceDTO> workspaces = workspaceService.getAllWorkspaces(userId);
        return ResponseEntity.ok(workspaces);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceDTO> updateWorkspace(
        @PathVariable long id,
        @RequestBody WorkspaceCreateDTO workspaceCreateDTO
    ) {
        try {
            CustomUserDetails userDetails = userService.auth();
            long workspaceOwner = workspaceService.getWorkspaceUserId(id);

            if(workspaceOwner != userDetails.getId())
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            WorkspaceDTO updatedWorkspace = workspaceService.updateWorkspace(id, workspaceCreateDTO);
            return ResponseEntity.ok(updatedWorkspace);
        } catch (WorkspaceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkspace(@PathVariable long id) {
        try {
            CustomUserDetails userDetails = userService.auth();
            long workspaceOwner = workspaceService.getWorkspaceUserId(id);

            if(workspaceOwner != userDetails.getId())
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            workspaceService.deleteWorkspace(id, userDetails.getId());
            return ResponseEntity.noContent().build();
        } catch (WorkspaceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MinWorkspacesLimitException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
