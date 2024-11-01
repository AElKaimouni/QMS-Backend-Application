package com.example.qms.workspace.services;


import com.example.qms.user.User;
import com.example.qms.workspace.Workspace;
import com.example.qms.workspace.WorkspaceRepository;
import com.example.qms.workspace.dto.WorkspaceCreateDTO;
import com.example.qms.workspace.dto.WorkspaceDTO;
import com.example.qms.workspace.exceptions.MaxWorkspacesLimitException;
import com.example.qms.workspace.exceptions.MinWorkspacesLimitException;
import com.example.qms.workspace.exceptions.WorkspaceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    private final int maxWorkspaces = 3;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    public WorkspaceDTO createWorkspace(WorkspaceCreateDTO workspaceCreateDTO, long userId) throws MaxWorkspacesLimitException {
        int workspacesCount = workspaceRepository.countByUserId(userId);

        if(workspacesCount >= maxWorkspaces) throw new MaxWorkspacesLimitException();

        Workspace workspace = convertToEntity(workspaceCreateDTO);

        workspace.setUserId(userId);

        Workspace savedWorkspace = workspaceRepository.save(workspace);
        return convertToDTO(savedWorkspace);
    }

    public Workspace getWorkspace(long id) {
        Optional<Workspace> workspace = workspaceRepository.findById(id);

        if(workspace.isEmpty()) throw new WorkspaceNotFoundException();

        return workspace.get();
    }

    public List<WorkspaceDTO> getAllWorkspaces(long userId) {
        return workspaceRepository.findAllByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public WorkspaceDTO updateWorkspace(long id, WorkspaceCreateDTO workspaceCreateDTO) throws WorkspaceNotFoundException {
        Workspace workspace = getWorkspace(id);
        updateEntityWithDTO(workspace, workspaceCreateDTO);
        Workspace updatedWorkspace = workspaceRepository.save(workspace);
        return convertToDTO(updatedWorkspace);
    }

    public void deleteWorkspace(long id, long userId) throws MinWorkspacesLimitException {
        int count = workspaceRepository.countByUserId(userId);

        if(count <= 1) throw new MinWorkspacesLimitException();

        workspaceRepository.deleteById(id);
    }

    public long getWorkspaceUserId(long workspaceId) {
        Optional<Long> userId = workspaceRepository.findUserIdById(workspaceId);

        if(userId.isEmpty()) throw new WorkspaceNotFoundException();

        return userId.get();
    }



    // Manual mapping methods

    private Workspace convertToEntity(WorkspaceCreateDTO dto) {
        Workspace workspace = new Workspace();

        workspace.setTitle(dto.getTitle());
        workspace.setBusinessName(dto.getBusinessName());
        workspace.setBusinessIndustry(dto.getBusinessIndustry());
        workspace.setContactEmail(dto.getContactEmail());
        workspace.setContactPhone(dto.getContactPhone());

        return workspace;
    }

    public WorkspaceDTO convertToDTO(Workspace workspace) {
        WorkspaceDTO dto = new WorkspaceDTO();
        dto.setId(workspace.getId());
        dto.setUserId(workspace.getUserId());
        dto.setTitle(workspace.getTitle());
        dto.setBusinessName(workspace.getBusinessName());
        dto.setBusinessIndustry(workspace.getBusinessIndustry());
        dto.setContactEmail(workspace.getContactEmail());
        dto.setContactPhone(workspace.getContactPhone());
        return dto;
    }

    private void updateEntityWithDTO(Workspace workspace, WorkspaceCreateDTO dto) {
        if(dto.getTitle() != null) workspace.setTitle(dto.getTitle());
        if(dto.getBusinessName() != null) workspace.setBusinessName(dto.getBusinessName());
        if(dto.getBusinessIndustry() != null) workspace.setBusinessIndustry(dto.getBusinessIndustry());
        if(dto.getContactEmail() != null) workspace.setContactEmail(dto.getContactEmail());
        if(dto.getContactPhone() != null) workspace.setContactPhone(dto.getContactPhone());
    }
}
