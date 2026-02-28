package com.project.splitwise.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
public class ExpenseUserDivision {

    // Map of userName to percentage (e.g., {sparsh: 40.0, mohit: 60.0})
    private Map<String, Double> userPercentages;
}
