package com.gymmanage.checkin.dto;

import jakarta.validation.constraints.NotNull;

public class CheckinSaveRequest {
    @NotNull
    private Long memberId;

    private String remark;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

