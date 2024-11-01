package com.example.qms.workspace.dto;

import lombok.Data;

@Data
public class WorkspaceDTO {
    private long id;
    private long userId;
    private String title;
    private String businessName;
    private String businessIndustry;
    private String contactEmail;
    private String contactPhone;
}