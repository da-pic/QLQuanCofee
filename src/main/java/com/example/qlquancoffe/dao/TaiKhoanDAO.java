package com.example.qlquancoffe.dao;

import com.example.qlquancoffe.models.TaiKhoan;
import com.example.qlquancoffe.models.TaiKhoan.TrangThai;
import com.example.qlquancoffe.models.TaiKhoan.VaiTro;
import com.example.qlquancoffe.utils.DatabaseConnection;
import com.example.qlquancoffe.utils.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * DAO xử lý thao tác CSDL cho bảng taikhoan
 */
public class TaiKhoanDAO {

    /**
     * Kiểm tra đăng nhập
     * @param username Tên đăng nhập
     * @param password Mật khẩu (chưa mã hóa)
     * @return Đối tượng TaiKhoan nếu đăng nhập thành công, null nếu thất bại
     */
    public TaiKhoan checkLogin(String username, String password) {
        String sql = "SELECT * FROM taikhoan WHERE ten_dang_nhap = ? AND trang_thai = 'DangLamViec'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("mat_khau");

                // Kiểm tra mật khẩu
                if (PasswordUtil.checkPassword(password, hashedPassword)) {
                    System.out.println("✅ Đăng nhập thành công: " + username);
                    return extractTaiKhoanFromResultSet(rs);
                } else {
                    System.out.println("❌ Sai mật khẩu");
                }
            } else {
                System.out.println("❌ Tài khoản không tồn tại hoặc đã bị khóa");
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi kiểm tra đăng nhập: " + e.getMessage());
        }

        return null;
    }

    /**
     * Lấy tất cả tài khoản
     * @return ObservableList chứa các tài khoản
     */
    public ObservableList<TaiKhoan> getAllTaiKhoan() {
        ObservableList<TaiKhoan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM taikhoan ORDER BY ngay_tao DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractTaiKhoanFromResultSet(rs));
            }

            System.out.println("✅ Đã load " + list.size() + " tài khoản");

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách tài khoản: " + e.getMessage());
        }

        return list;
    }

    /**
     * Lấy danh sách nhân viên đang làm việc
     */
    public ObservableList<TaiKhoan> getNhanVienDangLam() {
        ObservableList<TaiKhoan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM taikhoan WHERE trang_thai = 'DangLamViec' ORDER BY ho_ten";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractTaiKhoanFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách nhân viên: " + e.getMessage());
        }

        return list;
    }

    /**
     * Lấy tài khoản theo ID
     * @param id ID nhân viên
     * @return Đối tượng TaiKhoan hoặc null nếu không tìm thấy
     */
    public TaiKhoan getTaiKhoanById(int id) {
        String sql = "SELECT * FROM taikhoan WHERE id_nhanvien = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractTaiKhoanFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy tài khoản: " + e.getMessage());
        }

        return null;
    }

    /**
     * Thêm tài khoản mới
     * @param taiKhoan Đối tượng TaiKhoan (mật khẩu đã mã hóa)
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addTaiKhoan(TaiKhoan taiKhoan) {
        String sql = "INSERT INTO taikhoan(ho_ten, ten_dang_nhap, mat_khau, vai_tro, trang_thai) " +
                "VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, taiKhoan.getHoTen());
            pstmt.setString(2, taiKhoan.getTenDangNhap());
            pstmt.setString(3, taiKhoan.getMatKhau()); // Phải đã hash trước
            pstmt.setString(4, taiKhoan.getVaiTro().name());
            pstmt.setString(5, taiKhoan.getTrangThai().name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    taiKhoan.setIdNhanVien(generatedKeys.getInt(1));
                }
                System.out.println("✅ Thêm tài khoản thành công: " + taiKhoan.getTenDangNhap());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm tài khoản: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("💡 Tên đăng nhập đã tồn tại!");
            }
        }

        return false;
    }

    /**
     * Cập nhật thông tin tài khoản (không cập nhật mật khẩu)
     */
    public boolean updateTaiKhoan(TaiKhoan taiKhoan) {
        String sql = "UPDATE taikhoan SET ho_ten = ?, vai_tro = ?, trang_thai = ? " +
                "WHERE id_nhanvien = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, taiKhoan.getHoTen());
            pstmt.setString(2, taiKhoan.getVaiTro().name());
            pstmt.setString(3, taiKhoan.getTrangThai().name());
            pstmt.setInt(4, taiKhoan.getIdNhanVien());

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Cập nhật tài khoản thành công");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật tài khoản: " + e.getMessage());
        }

        return false;
    }

    /**
     * Đổi mật khẩu
     * @param idNhanVien ID nhân viên
     * @param oldPassword Mật khẩu cũ (plain text)
     * @param newPassword Mật khẩu mới (plain text)
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean changePassword(int idNhanVien, String oldPassword, String newPassword) {
        // Kiểm tra mật khẩu cũ
        String checkSql = "SELECT mat_khau FROM taikhoan WHERE id_nhanvien = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, idNhanVien);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentHash = rs.getString("mat_khau");

                // Verify mật khẩu cũ
                if (!PasswordUtil.checkPassword(oldPassword, currentHash)) {
                    System.err.println("❌ Mật khẩu cũ không đúng!");
                    return false;
                }

                // Update mật khẩu mới
                String updateSql = "UPDATE taikhoan SET mat_khau = ? WHERE id_nhanvien = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, PasswordUtil.hashPassword(newPassword));
                    updateStmt.setInt(2, idNhanVien);

                    boolean success = updateStmt.executeUpdate() > 0;
                    if (success) {
                        System.out.println("✅ Đổi mật khẩu thành công");
                    }
                    return success;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi đổi mật khẩu: " + e.getMessage());
        }

        return false;
    }

    /**
     * Reset mật khẩu về mặc định (chỉ dành cho admin)
     * @param idNhanVien ID nhân viên
     * @param newPasswordHash Mật khẩu mới đã hash
     * @return true nếu thành công
     */
    public boolean resetPassword(int idNhanVien, String newPasswordHash) {
        String sql = "UPDATE taikhoan SET mat_khau = ? WHERE id_nhanvien = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, idNhanVien);

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Reset mật khẩu thành công");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi reset mật khẩu: " + e.getMessage());
        }

        return false;
    }

    /**
     * Xóa tài khoản (soft delete - chuyển trạng thái thành DaNghiViec)
     */
    public boolean deleteTaiKhoan(int id) {
        String sql = "UPDATE taikhoan SET trang_thai = 'DaNghiViec' WHERE id_nhanvien = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                System.out.println("✅ Xóa tài khoản thành công (soft delete)");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa tài khoản: " + e.getMessage());
        }

        return false;
    }

    /**
     * Xóa vĩnh viễn tài khoản (hard delete - CHỈ khi chắc chắn)
     */
    public boolean permanentDeleteTaiKhoan(int id) {
        String sql = "DELETE FROM taikhoan WHERE id_nhanvien = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                System.out.println("✅ Xóa vĩnh viễn tài khoản");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa tài khoản: " + e.getMessage());
            System.err.println("💡 Tài khoản có thể đang có hóa đơn liên quan");
        }

        return false;
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa
     */
    public boolean isUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM taikhoan WHERE ten_dang_nhap = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi kiểm tra username: " + e.getMessage());
        }

        return false;
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại (trừ ID hiện tại - dùng khi update)
     */
    public boolean isUsernameExistExceptId(String username, int idNhanVien) {
        String sql = "SELECT COUNT(*) FROM taikhoan WHERE ten_dang_nhap = ? AND id_nhanvien != ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, idNhanVien);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi kiểm tra username: " + e.getMessage());
        }

        return false;
    }

    /**
     * Đếm số hóa đơn của nhân viên
     * @param idNhanVien ID nhân viên
     * @return Số lượng hóa đơn
     */
    public int countHoaDon(int idNhanVien) {
        String sql = "SELECT COUNT(*) FROM hoadon WHERE id_nhanvien = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idNhanVien);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi đếm hóa đơn: " + e.getMessage());
        }

        return 0;
    }

    // ==================== HELPER METHOD ====================

    /**
     * Trích xuất đối tượng TaiKhoan từ ResultSet
     */
    private TaiKhoan extractTaiKhoanFromResultSet(ResultSet rs) throws SQLException {
        return new TaiKhoan(
                rs.getInt("id_nhanvien"),
                rs.getString("ho_ten"),
                rs.getString("ten_dang_nhap"),
                rs.getString("mat_khau"),
                VaiTro.valueOf(rs.getString("vai_tro")),
                TrangThai.valueOf(rs.getString("trang_thai")),
                rs.getTimestamp("ngay_tao").toLocalDateTime()
        );
    }
}