package com.example.qlquancoffe.dao;

import com.example.qlquancoffe.models.ChiTietHoaDon;
import com.example.qlquancoffe.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.math.BigDecimal;

/**
 * DAO xử lý thao tác CSDL cho bảng chitiethoadon
 */
public class ChiTietHoaDonDAO {

    /**
     * Lấy chi tiết hóa đơn theo ID hóa đơn
     */
    public ObservableList<ChiTietHoaDon> getChiTietByHoaDon(int idHoaDon) {
        ObservableList<ChiTietHoaDon> list = FXCollections.observableArrayList();

        String sql = """
            SELECT c.*, s.ten_sanpham 
            FROM chitiethoadon c
            LEFT JOIN sanpham s ON c.id_sanpham = s.id_sanpham
            WHERE c.id_hoadon = ?
            ORDER BY c.id_sanpham
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHoaDon);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChiTietHoaDon ct = extractChiTietFromResultSet(rs);
                ct.setTenSanPham(rs.getString("ten_sanpham"));
                list.add(ct);
            }

            System.out.println("✅ Đã load " + list.size() + " chi tiết hóa đơn #" + idHoaDon);

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy chi tiết cụ thể (1 sản phẩm trong 1 hóa đơn)
     */
    public ChiTietHoaDon getChiTiet(int idHoaDon, int idSanPham) {
        String sql = """
            SELECT c.*, s.ten_sanpham 
            FROM chitiethoadon c
            LEFT JOIN sanpham s ON c.id_sanpham = s.id_sanpham
            WHERE c.id_hoadon = ? AND c.id_sanpham = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHoaDon);
            pstmt.setInt(2, idSanPham);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ChiTietHoaDon ct = extractChiTietFromResultSet(rs);
                ct.setTenSanPham(rs.getString("ten_sanpham"));
                return ct;
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy chi tiết: " + e.getMessage());
        }

        return null;
    }

    /**
     * Thêm chi tiết hóa đơn
     */
    public boolean addChiTiet(ChiTietHoaDon chiTiet) {
        String sql = "INSERT INTO chitiethoadon(id_hoadon, id_sanpham, so_luong, don_gia, thanh_tien) " +
                "VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, chiTiet.getIdHoaDon());
            pstmt.setInt(2, chiTiet.getIdSanPham());
            pstmt.setInt(3, chiTiet.getSoLuong());
            pstmt.setBigDecimal(4, chiTiet.getDonGia());
            pstmt.setBigDecimal(5, chiTiet.getThanhTien());

            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                System.out.println("✅ Thêm chi tiết hóa đơn thành công");

                // Tự động trừ tồn kho
                SanPhamDAO sanPhamDAO = new SanPhamDAO();
                sanPhamDAO.giamTonKho(chiTiet.getIdSanPham(), chiTiet.getSoLuong());

                // Cập nhật tổng tiền hóa đơn
                HoaDonDAO hoaDonDAO = new HoaDonDAO();
                hoaDonDAO.updateTongTien(chiTiet.getIdHoaDon());
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm chi tiết hóa đơn: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("💡 Sản phẩm đã có trong hóa đơn! Hãy cập nhật số lượng.");
            }
        }

        return false;
    }

    /**
     * Thêm nhiều chi tiết cùng lúc (cho bán hàng)
     */
    public boolean addMultipleChiTiet(int idHoaDon, ObservableList<ChiTietHoaDon> danhSach) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            String sql = "INSERT INTO chitiethoadon(id_hoadon, id_sanpham, so_luong, don_gia, thanh_tien) " +
                    "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            SanPhamDAO sanPhamDAO = new SanPhamDAO();

            for (ChiTietHoaDon ct : danhSach) {
                ct.setIdHoaDon(idHoaDon);

                pstmt.setInt(1, ct.getIdHoaDon());
                pstmt.setInt(2, ct.getIdSanPham());
                pstmt.setInt(3, ct.getSoLuong());
                pstmt.setBigDecimal(4, ct.getDonGia());
                pstmt.setBigDecimal(5, ct.getThanhTien());

                pstmt.addBatch();

                // Trừ tồn kho
                sanPhamDAO.giamTonKho(ct.getIdSanPham(), ct.getSoLuong());
            }

            pstmt.executeBatch();

            // Cập nhật tổng tiền
            HoaDonDAO hoaDonDAO = new HoaDonDAO();
            hoaDonDAO.updateTongTien(idHoaDon);

            conn.commit(); // Commit transaction
            System.out.println("✅ Thêm " + danhSach.size() + " chi tiết hóa đơn thành công");
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm nhiều chi tiết: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu lỗi
                    System.out.println("🔄 Đã rollback transaction");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    /**
     * Cập nhật số lượng sản phẩm trong hóa đơn
     */
    public boolean updateSoLuong(int idHoaDon, int idSanPham, int soLuongMoi) {
        // Lấy số lượng cũ
        ChiTietHoaDon chiTietCu = getChiTiet(idHoaDon, idSanPham);
        if (chiTietCu == null) {
            System.err.println("❌ Không tìm thấy chi tiết hóa đơn");
            return false;
        }

        int soLuongCu = chiTietCu.getSoLuong();
        int chenhLech = soLuongMoi - soLuongCu;

        String sql = """
            UPDATE chitiethoadon 
            SET so_luong = ?, thanh_tien = don_gia * ?
            WHERE id_hoadon = ? AND id_sanpham = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, soLuongMoi);
            pstmt.setInt(2, soLuongMoi);
            pstmt.setInt(3, idHoaDon);
            pstmt.setInt(4, idSanPham);

            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                System.out.println("✅ Cập nhật số lượng thành công");

                // Cập nhật tồn kho
                SanPhamDAO sanPhamDAO = new SanPhamDAO();
                if (chenhLech > 0) {
                    sanPhamDAO.giamTonKho(idSanPham, chenhLech);
                } else if (chenhLech < 0) {
                    sanPhamDAO.tangTonKho(idSanPham, Math.abs(chenhLech));
                }

                // Cập nhật tổng tiền
                HoaDonDAO hoaDonDAO = new HoaDonDAO();
                hoaDonDAO.updateTongTien(idHoaDon);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật số lượng: " + e.getMessage());
        }

        return false;
    }

    /**
     * Xóa chi tiết hóa đơn
     */
    public boolean deleteChiTiet(int idHoaDon, int idSanPham) {
        // Lấy thông tin chi tiết trước khi xóa
        ChiTietHoaDon chiTiet = getChiTiet(idHoaDon, idSanPham);
        if (chiTiet == null) {
            System.err.println("❌ Không tìm thấy chi tiết để xóa");
            return false;
        }

        String sql = "DELETE FROM chitiethoadon WHERE id_hoadon = ? AND id_sanpham = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHoaDon);
            pstmt.setInt(2, idSanPham);

            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                System.out.println("✅ Xóa chi tiết hóa đơn thành công");

                // Hoàn trả tồn kho
                SanPhamDAO sanPhamDAO = new SanPhamDAO();
                sanPhamDAO.tangTonKho(idSanPham, chiTiet.getSoLuong());

                // Cập nhật tổng tiền
                HoaDonDAO hoaDonDAO = new HoaDonDAO();
                hoaDonDAO.updateTongTien(idHoaDon);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa chi tiết: " + e.getMessage());
        }

        return false;
    }

    /**
     * Xóa tất cả chi tiết của hóa đơn
     */
    public boolean deleteAllChiTiet(int idHoaDon) {
        // Lấy danh sách chi tiết trước khi xóa
        ObservableList<ChiTietHoaDon> danhSach = getChiTietByHoaDon(idHoaDon);

        String sql = "DELETE FROM chitiethoadon WHERE id_hoadon = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHoaDon);
            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                System.out.println("✅ Xóa tất cả chi tiết hóa đơn");

                // Hoàn trả tồn kho
                SanPhamDAO sanPhamDAO = new SanPhamDAO();
                for (ChiTietHoaDon ct : danhSach) {
                    sanPhamDAO.tangTonKho(ct.getIdSanPham(), ct.getSoLuong());
                }

                // Cập nhật tổng tiền về 0
                HoaDonDAO hoaDonDAO = new HoaDonDAO();
                hoaDonDAO.updateTongTien(idHoaDon);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa chi tiết hóa đơn: " + e.getMessage());
        }

        return false;
    }

    /**
     * Kiểm tra sản phẩm đã có trong hóa đơn chưa
     */
    public boolean isExist(int idHoaDon, int idSanPham) {
        String sql = "SELECT COUNT(*) FROM chitiethoadon WHERE id_hoadon = ? AND id_sanpham = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHoaDon);
            pstmt.setInt(2, idSanPham);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi kiểm tra chi tiết: " + e.getMessage());
        }

        return false;
    }

    /**
     * Đếm số sản phẩm trong hóa đơn
     */
    public int countSanPham(int idHoaDon) {
        String sql = "SELECT COUNT(*) FROM chitiethoadon WHERE id_hoadon = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHoaDon);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi đếm sản phẩm: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Tính tổng tiền của hóa đơn từ chi tiết
     */
    public BigDecimal calculateTongTien(int idHoaDon) {
        String sql = "SELECT COALESCE(SUM(thanh_tien), 0) FROM chitiethoadon WHERE id_hoadon = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHoaDon);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi tính tổng tiền: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    // ==================== HELPER METHOD ====================

    /**
     * Trích xuất đối tượng ChiTietHoaDon từ ResultSet
     */
    private ChiTietHoaDon extractChiTietFromResultSet(ResultSet rs) throws SQLException {
        return new ChiTietHoaDon(
                rs.getInt("id_hoadon"),
                rs.getInt("id_sanpham"),
                rs.getInt("so_luong"),
                rs.getBigDecimal("don_gia"),
                rs.getBigDecimal("thanh_tien")
        );
    }
}