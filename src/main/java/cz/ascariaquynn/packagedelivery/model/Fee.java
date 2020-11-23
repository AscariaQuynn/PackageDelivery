package cz.ascariaquynn.packagedelivery.model;

import java.math.BigDecimal;

public class Fee {

    private BigDecimal maxWeight;
    private BigDecimal fee;

    public Fee(BigDecimal maxWeight, BigDecimal fee) {
        this.maxWeight = maxWeight;
        this.fee = fee;
    }
}
