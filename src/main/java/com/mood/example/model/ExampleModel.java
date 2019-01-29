package com.mood.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Setter
@Getter
@ToString
public class ExampleModel {
    private Integer id ;
    private Long cpOrderId ;//1
    private Integer examineStatus ;//1
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date examineTime ;//
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date succeedTime;//
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date  proposeTime;//
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;//
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;//
    private String city ;//1
    private String source ;//1
}
