package cz.ascariaquynn.packagedelivery.model;

import java.math.BigDecimal;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterimPackage that = (InterimPackage) o;
        return postCode.equals(that.postCode) &&
                weight.equals(that.weight) &&
                Objects.equals(fee, that.fee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postCode, weight, fee);
    }

    @Override
    public String toString() {
        return getPrintableFormatted();
    }
}
