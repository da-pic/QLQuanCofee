package com.example.qlquancoffe.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Lớp tiện ích format tiền tệ
 */
public class CurrencyUtil {

    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    /**
     * Format số tiền theo định dạng Việt Nam
     * @param amount Số tiền (BigDecimal)
     * @return String đã format (VD: "25,000 ₫")
     */
    public static String formatVND(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        return formatter.format(amount) + " ₫";
    }

    /**
     * Format số tiền (double)
     */
    public static String formatVND(double amount) {
        return formatter.format(amount) + " ₫";
    }

    /**
     * Format số tiền (int)
     */
    public static String formatVND(int amount) {
        return formatter.format(amount) + " ₫";
    }

    /**
     * Format số tiền không có ký hiệu ₫
     */
    public static String formatNumber(BigDecimal amount) {
        if (amount == null) return "0";
        return formatter.format(amount);
    }
}