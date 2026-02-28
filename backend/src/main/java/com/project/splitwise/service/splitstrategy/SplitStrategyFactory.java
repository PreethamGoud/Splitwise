package com.project.splitwise.service.splitstrategy;

import com.project.splitwise.entity.SplitType;

public class SplitStrategyFactory {

	public static SplitStrategy getStrategy(SplitType type) {
        switch (type) {
            case EQUAL: return new EqualSplitStrategy();
            case EXACT: return new ExactSplitStrategy();
            case PERCENTAGE: return new PercentageSplitStrategy();
            default: throw new IllegalArgumentException("Invalid split type");
        }
    }
}
