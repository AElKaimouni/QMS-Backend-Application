package com.example.qms.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WorkspaceCreateDTO {
    @NotEmpty
    @NotBlank(message = "Title is required")
    private String title;
    private String businessName;
    private String businessIndustry;
    private String contactEmail;
    private String contactPhone;
}