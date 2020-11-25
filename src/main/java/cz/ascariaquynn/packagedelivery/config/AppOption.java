package cz.ascariaquynn.packagedelivery.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AppOption {

    public static final int PAD_SIZE = 32;

    private String opt;
    private String longOpt;
    private String arg;
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

    public boolean hasArg() {
        return null != arg;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
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

    public String getPrintableFormatted() {
        String output = "--" + getOpt();
        output += hasArg() ? "=<" + getArg() + ">" : "";
        output += ", --" + getLongOpt();
        output += hasArg() ? "=<" + getArg() + ">" : "";
        output = StringUtils.rightPad(output, PAD_SIZE);
        output += description + (isRequired() ? " (required)" : "");
        output = " " + output;
        return output;
    }

    public String getPrintableArgFormatted() {
        String output = "--" + getOpt();
        output += hasArg() ? "=<" + getArg() + ">" : "";
        output = !isRequired() ? "[" + output + "]" : output;
        output = " " + output;
        return output;
    }

    public int compareTo(AppOption o2) {
        if(isRequired() && !o2.isRequired()) {
            return -1;
        } else if(!isRequired() && o2.isRequired()) {
            return 1;
        } else {
            return getLongOpt().compareTo(o2.getLongOpt());
        }
    }

    @Override
    public String toString() {
        return "AppOption[opt: " + getOpt() + ", " +
                "longOpt: " + getLongOpt() + ", " +
                "arg: " + (hasArg() ? getArg() : "<no arg>") + ", " +
                "required: " + Boolean.toString(isRequired()).toUpperCase() +
                "]";
    }
}
