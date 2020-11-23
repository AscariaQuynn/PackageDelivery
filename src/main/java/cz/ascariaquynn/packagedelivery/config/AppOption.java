package cz.ascariaquynn.packagedelivery.config;

import org.springframework.stereotype.Component;

@Component
public class AppOption {

    private String opt;
    private String longOpt;
    private boolean required;
    private String description;
    private String inputRegex;

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getLongOpt() {
        return longOpt;
    }

    public void setLongOpt(String longOpt) {
        this.longOpt = longOpt;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputRegex() {
        return inputRegex;
    }

    public void setInputRegex(String inputRegex) {
        this.inputRegex = inputRegex;
    }

    @Override
    public String toString() {
        return "AppOption[opt: " + getOpt() + ", " +
                "longOpt: " + getLongOpt() + "," +
                "required: " + Boolean.toString(isRequired()).toUpperCase() +
                "]";
    }
}
