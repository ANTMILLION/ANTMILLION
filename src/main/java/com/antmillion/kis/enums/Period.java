package com.antmillion.kis.enums;

public enum Period {
    DAY("D"), WEEK("W"), MONTH("M"), YEAR("Y");

    private final String value;

    Period(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
