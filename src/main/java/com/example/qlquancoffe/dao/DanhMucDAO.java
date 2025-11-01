package com.example.qlquancoffe.dao;

import com.example.qlquancoffe.models.DanhMuc;
import com.example.qlquancoffe.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * DAO xử lý thao tác CSDL cho bảng danhmuc
 */
public class DanhMucDAO {

    /**
     * Lấy tất cả danh mục
     * @return ObservableList chứa các danh mục
     */
    public ObservableList<DanhMuc> getAllDanhMuc() {
        ObservableList<DanhMuc> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM danhmuc ORDER BY ten_danhmuc";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DanhMuc dm = new DanhMuc(
                        rs.getInt("id_danhmuc"),
                        rs.getString("ten_danhmuc")
                );
                list.add(dm);
            }

            System.out.println("✅ Đã load " + list.size() + " danh mục");

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách danh mục: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy danh mục theo ID
     * @param id ID danh mục
     * @return Đối tượng DanhMuc hoặc null nếu không tìm thấy
     */
    public DanhMuc getDanhMucById(int id) {
        String sql = "SELECT * FROM danhmuc WHERE id_danhmuc = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new DanhMuc(
                        rs.getInt("id_danhmuc"),
                        rs.getString("ten_danhmuc")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh mục: " + e.getMessage());
        }

        return null;
    }

    /**
     * Thêm danh mục mới
     * @param danhMuc Đối tượng DanhMuc cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addDanhMuc(DanhMuc danhMuc) {
        String sql = "INSERT INTO danhmuc(ten_danhmuc) VALUES(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, danhMuc.getTenDanhMuc());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    danhMuc.setIdDanhMuc(generatedKeys.getInt(1));
                }
                System.out.println("✅ Thêm danh mục thành công: " + danhMuc.getTenDanhMuc());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm danh mục: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("💡 Tên danh mục đã tồn tại!");
            }
        }

        return false;
    }

    /**
     * Cập nhật danh mục
     * @param danhMuc Đối tượng DanhMuc cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateDanhMuc(DanhMuc danhMuc) {
        String sql = "UPDATE danhmuc SET ten_danhmuc = ? WHERE id_danhmuc = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, danhMuc.getTenDanhMuc());
            pstmt.setInt(2, danhMuc.getIdDanhMuc());

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Cập nhật danh mục thành công");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật danh mục: " + e.getMessage());
        }

        return false;
    }

    /**
     * Xóa danh mục
     * @param id ID danh mục cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteDanhMuc(int id) {
        String sql = "DELETE FROM danhmuc WHERE id_danhmuc = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                System.out.println("✅ Xóa danh mục thành công");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa danh mục: " + e.getMessage());
            System.err.println("💡 Có thể danh mục đang được sử dụng bởi sản phẩm");
        }

        return false;
    }

    /**
     * Kiểm tra danh mục có tồn tại không
     * @param id ID danh mục
     * @return true nếu tồn tại, false nếu không
     */
    public boolean isExist(int id) {
        String sql = "SELECT COUNT(*) FROM danhmuc WHERE id_danhmuc = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi kiểm tra danh mục: " + e.getMessage());
        }

        return false;
    }

    /**
     * Đếm số sản phẩm trong danh mục
     * @param id ID danh mục
     * @return Số lượng sản phẩm
     */
    public int countSanPham(int id) {
        String sql = "SELECT COUNT(*) FROM sanpham WHERE id_danhmuc = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi đếm sản phẩm: " + e.getMessage());
        }

        return 0;
    }
}