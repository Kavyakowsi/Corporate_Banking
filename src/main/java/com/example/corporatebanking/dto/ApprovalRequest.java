package com.example.corporatebanking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApprovalRequest {

    @JsonAlias({"id", "fundTransferId"})
    private String fundTransferId;

    @NotBlank
    @JsonAlias({"Username", "userName"})
    private String username;

    @JsonAlias({"Approve"})
    private String approve = "true";

    public String getFundTransferId() {
        return fundTransferId;
    }

    public void setFundTransferId(String fundTransferId) {
        this.fundTransferId = fundTransferId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    @Override
    public String toString() {
        return "ApprovalRequest{" +
                "fundTransferId='" + fundTransferId + '\'' +
                ", username='" + username + '\'' +
                ", approve='" + approve + '\'' +
                '}';
    }
}