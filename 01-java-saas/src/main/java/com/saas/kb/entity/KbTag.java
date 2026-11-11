package com.saas.kb.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 标签实体
 */
@Data
@TableName("kb_tag")
public class KbTag {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long tenantId;

    @NotBlank(message = "标签名不能为空")
    private String name;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createTime;
}
