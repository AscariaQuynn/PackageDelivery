package cz.ascariaquynn.packagedelivery.model;

import java.math.BigDecimal;

public class Package {

    private String postCode;

    private BigDecimal weight;

    private BigDecimal fee;

    public Package(String postCode, BigDecimal weight) {
        this(postCode, weight, null);
    }

    public Package(String postCode, BigDecimal weight, BigDecimal fee) {
        this.postCode = postCode;
        this.weight = weight;
        this.fee = fee;
    }

    public String getPostCode() {
        return postCode;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public BigDecimal getFee() {
        return fee;
    }

    @Override
    public String toString() {
        return "Package[" +
                "postcode: " + postCode +
                ", weight: " + weight +
                ", fee: " + (null != fee ? fee : "NULL") +
                "]";
    }
}
