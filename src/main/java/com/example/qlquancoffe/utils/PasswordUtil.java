package com.example.qlquancoffe.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Lớp tiện ích mã hóa và xác thực mật khẩu
 */
public class PasswordUtil {

    /**
     * Mã hóa mật khẩu sử dụng BCrypt
     * @param plainPassword Mật khẩu gốc
     * @return Mật khẩu đã mã hóa
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    /**
     * Kiểm tra mật khẩu có khớp với hash không
     * @param plainPassword Mật khẩu người dùng nhập
     * @param hashedPassword Mật khẩu đã hash trong database
     * @return true nếu khớp, false nếu không
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Tạo mật khẩu mặc định đã mã hóa (dùng cho tạo tài khoản mới)
     * @return Hash của mật khẩu "123456"
     */
    public static String getDefaultPasswordHash() {
        return hashPassword("123456");
    }
}