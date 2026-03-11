package com.yj2025.sequence.storage;

import lombok.Data;

import java.util.Date;

@Data
public class SequenceNoEntity {
    private Integer id;
    private String entCode;
    private String groupId;
    private Integer seqNum;
    private Boolean reverse;
    private Date createTime;
    private Date updateTime;

}
