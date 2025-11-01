package com.example.qlquancoffe.dao;

import com.example.qlquancoffe.models.HoaDon;
import com.example.qlquancoffe.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * DAO xử lý thao tác CSDL cho bảng hoadon
 */
public class HoaDonDAO {

    /**
     * Lấy tất cả hóa đơn
     */
    public ObservableList<HoaDon> getAllHoaDon() {
        ObservableList<HoaDon> list = FXCollections.observableArrayList();

        String sql = """
            SELECT h.*, t.ho_ten 
            FROM hoadon h
            LEFT JOIN taikhoan t ON h.id_nhanvien = t.id_nhanvien
            ORDER BY h.ngay_tao DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HoaDon hd = extractHoaDonFromResultSet(rs);
                hd.setTenNhanVien(rs.getString("ho_ten"));
                list.add(hd);
            }

            System.out.println("✅ Đã load " + list.size() + " hóa đơn");

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy hóa đơn theo ID
     */
    public HoaDon getHoaDonById(int id) {
        String sql = """
            SELECT h.*, t.ho_ten 
            FROM hoadon h
            LEFT JOIN taikhoan t ON h.id_nhanvien = t.id_nhanvien
            WHERE h.id_hoadon = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                HoaDon hd = extractHoaDonFromResultSet(rs);
                hd.setTenNhanVien(rs.getString("ho_ten"));
                return hd;
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy hóa đơn: " + e.getMessage());
        }

        return null;
    }

    /**
     * Lấy hóa đơn theo nhân viên
     */
    public ObservableList<HoaDon> getHoaDonByNhanVien(int idNhanVien) {
        ObservableList<HoaDon> list = FXCollections.observableArrayList();

        String sql = """
            SELECT h.*, t.ho_ten 
            FROM hoadon h
            LEFT JOIN taikhoan t ON h.id_nhanvien = t.id_nhanvien
            WHERE h.id_nhanvien = ?
            ORDER BY h.ngay_tao DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idNhanVien);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = extractHoaDonFromResultSet(rs);
                hd.setTenNhanVien(rs.getString("ho_ten"));
                list.add(hd);
            }

            System.out.println("✅ Đã load " + list.size() + " hóa đơn của nhân viên #" + idNhanVien);

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy hóa đơn theo nhân viên: " + e.getMessage());
        }

        return list;
    }

    /**
     * Lấy hóa đơn theo ngày
     */
    public ObservableList<HoaDon> getHoaDonByDate(LocalDate date) {
        ObservableList<HoaDon> list = FXCollections.observableArrayList();

        String sql = """
            SELECT h.*, t.ho_ten 
            FROM hoadon h
            LEFT JOIN taikhoan t ON h.id_nhanvien = t.id_nhanvien
            WHERE DATE(h.ngay_tao) = ?
            ORDER BY h.ngay_tao DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = extractHoaDonFromResultSet(rs);
                hd.setTenNhanVien(rs.getString("ho_ten"));
                list.add(hd);
            }

            System.out.println("✅ Đã load " + list.size() + " hóa đơn ngày " + date);

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy hóa đơn theo ngày: " + e.getMessage());
        }

        return list;
    }

    /**
     * Lấy hóa đơn theo khoảng thời gian
     */
    public ObservableList<HoaDon> getHoaDonByDateRange(LocalDate fromDate, LocalDate toDate) {
        ObservableList<HoaDon> list = FXCollections.observableArrayList();

        String sql = """
            SELECT h.*, t.ho_ten 
            FROM hoadon h
            LEFT JOIN taikhoan t ON h.id_nhanvien = t.id_nhanvien
            WHERE DATE(h.ngay_tao) BETWEEN ? AND ?
            ORDER BY h.ngay_tao DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fromDate));
            pstmt.setDate(2, Date.valueOf(toDate));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = extractHoaDonFromResultSet(rs);
                hd.setTenNhanVien(rs.getString("ho_ten"));
                list.add(hd);
            }

            System.out.println("✅ Đã load " + list.size() + " hóa đơn từ " + fromDate + " đến " + toDate);

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy hóa đơn theo khoảng thời gian: " + e.getMessage());
        }

        return list;
    }

    /**
     * Lấy hóa đơn theo nhân viên và ngày
     */
    public ObservableList<HoaDon> getHoaDonByNhanVienAndDate(int idNhanVien, LocalDate date) {
        ObservableList<HoaDon> list = FXCollections.observableArrayList();

        String sql = """
            SELECT h.*, t.ho_ten 
            FROM hoadon h
            LEFT JOIN taikhoan t ON h.id_nhanvien = t.id_nhanvien
            WHERE h.id_nhanvien = ? AND DATE(h.ngay_tao) = ?
            ORDER BY h.ngay_tao DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idNhanVien);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = extractHoaDonFromResultSet(rs);
                hd.setTenNhanVien(rs.getString("ho_ten"));
                list.add(hd);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy hóa đơn: " + e.getMessage());
        }

        return list;
    }

    /**
     * Tạo hóa đơn mới
     * @param hoaDon Đối tượng HoaDon
     * @return ID của hóa đơn vừa tạo, hoặc -1 nếu thất bại
     */
    public int createHoaDon(HoaDon hoaDon) {
        String sql = "INSERT INTO hoadon(id_nhanvien, tong_tien, ghi_chu) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, hoaDon.getIdNhanVien());
            pstmt.setBigDecimal(2, hoaDon.getTongTien());
            pstmt.setString(3, hoaDon.getGhiChu());

            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    int id = keys.getInt(1);
                    hoaDon.setIdHoaDon(id);
                    System.out.println("✅ Tạo hóa đơn thành công, ID: " + id);
                    return id;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi tạo hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Cập nhật tổng tiền hóa đơn (tính từ chi tiết)
     */
    public boolean updateTongTien(int idHoaDon) {
        String sql = """
            UPDATE hoadon 
            SET tong_tien = (
                SELECT COALESCE(SUM(thanh_tien), 0) 
                FROM chitiethoadon 
                WHERE id_hoadon = ?
            )
            WHERE id_hoadon = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHoaDon);
            pstmt.setInt(2, idHoaDon);

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Cập nhật tổng tiền hóa đơn #" + idHoaDon);
            }
            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật tổng tiền: " + e.getMessage());
        }

        return false;
    }

    /**
     * Cập nhật ghi chú hóa đơn
     */
    public boolean updateGhiChu(int idHoaDon, String ghiChu) {
        String sql = "UPDATE hoadon SET ghi_chu = ? WHERE id_hoadon = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ghiChu);
            pstmt.setInt(2, idHoaDon);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật ghi chú: " + e.getMessage());
        }

        return false;
    }

    /**
     * Xóa hóa đơn (sẽ tự động xóa chi tiết do CASCADE)
     */
    public boolean deleteHoaDon(int id) {
        String sql = "DELETE FROM hoadon WHERE id_hoadon = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                System.out.println("✅ Xóa hóa đơn #" + id + " thành công");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa hóa đơn: " + e.getMessage());
        }

        return false;
    }

    /**
     * Tính tổng doanh thu theo ngày
     */
    public BigDecimal getTongDoanhThuByDate(LocalDate date) {
        String sql = "SELECT COALESCE(SUM(tong_tien), 0) FROM hoadon WHERE DATE(ngay_tao) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi tính doanh thu: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    /**
     * Tính tổng doanh thu theo khoảng thời gian
     */
    public BigDecimal getTongDoanhThuByDateRange(LocalDate fromDate, LocalDate toDate) {
        String sql = "SELECT COALESCE(SUM(tong_tien), 0) FROM hoadon WHERE DATE(ngay_tao) BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fromDate));
            pstmt.setDate(2, Date.valueOf(toDate));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi tính doanh thu: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    /**
     * Tính tổng doanh thu của nhân viên theo ngày
     */
    public BigDecimal getTongDoanhThuNhanVien(int idNhanVien, LocalDate date) {
        String sql = "SELECT COALESCE(SUM(tong_tien), 0) FROM hoadon WHERE id_nhanvien = ? AND DATE(ngay_tao) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idNhanVien);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi tính doanh thu nhân viên: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    /**
     * Đếm số hóa đơn theo ngày
     */
    public int countHoaDonByDate(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM hoadon WHERE DATE(ngay_tao) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi đếm hóa đơn: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Đếm số hóa đơn của nhân viên theo ngày
     */
    public int countHoaDonNhanVien(int idNhanVien, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM hoadon WHERE id_nhanvien = ? AND DATE(ngay_tao) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idNhanVien);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi đếm hóa đơn: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Lấy doanh thu theo tháng (cho biểu đồ)
     */
    public ObservableList<Object[]> getDoanhThuTheoThang(int year) {
        ObservableList<Object[]> list = FXCollections.observableArrayList();

        String sql = """
            SELECT MONTH(ngay_tao) as thang, SUM(tong_tien) as doanh_thu
            FROM hoadon
            WHERE YEAR(ngay_tao) = ?
            GROUP BY MONTH(ngay_tao)
            ORDER BY thang
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("thang"),
                        rs.getBigDecimal("doanh_thu")
                };
                list.add(row);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy doanh thu theo tháng: " + e.getMessage());
        }

        return list;
    }

    // ==================== HELPER METHOD ====================

    /**
     * Trích xuất đối tượng HoaDon từ ResultSet
     */
    private HoaDon extractHoaDonFromResultSet(ResultSet rs) throws SQLException {
        return new HoaDon(
                rs.getInt("id_hoadon"),
                rs.getInt("id_nhanvien"),
                rs.getTimestamp("ngay_tao").toLocalDateTime(),
                rs.getBigDecimal("tong_tien"),
                rs.getString("ghi_chu")
        );
    }
}