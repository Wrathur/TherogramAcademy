package com.wrathur.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticEvent implements Serializable {
    private String type; // 事件类型
    private List<Integer> userIds; // 用户ID列表
}