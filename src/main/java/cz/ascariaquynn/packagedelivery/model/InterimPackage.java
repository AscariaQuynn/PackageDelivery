package cz.ascariaquynn.packagedelivery.model;

import java.math.BigDecimal;

public class InterimPackage {

    private String postCode;

    private BigDecimal weight = BigDecimal.ZERO;

    private BigDecimal fee = null;

    public InterimPackage(String postCode) {
        this.postCode = postCode;
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

    public void merge(Package aPackage) {
        if(!postCode.equals(aPackage.getPostCode())) {
            throw new InterimMergeException("Post code '" + aPackage.getPostCode() + "' not equal to '" + postCode + "'.");
        }
        weight = weight.add(aPackage.getWeight());
        if(null != aPackage.getFee()) {
            fee = fee != null ? fee.add(aPackage.getFee()) : aPackage.getFee();
        }
    }

    public String getPrintableFormatted() {
        return postCode + " " + weight.setScale(3) + (fee != null ? " " + fee.setScale(3) : "");
    }

    @Override
    public String toString() {
        return getPrintableFormatted();
    }
}
