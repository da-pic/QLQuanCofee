package com.example.qlquancoffe.models;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDon {
    private final IntegerProperty idHoaDon;
    private final IntegerProperty idNhanVien;
    private final ObjectProperty<LocalDateTime> ngayTao;
    private final ObjectProperty<BigDecimal> tongTien;
    private final StringProperty ghiChu;

    // Thêm property cho tên nhân viên (để hiển thị)
    private final StringProperty tenNhanVien;

    // Constructor đầy đủ
    public HoaDon(int idHoaDon, int idNhanVien, LocalDateTime ngayTao,
                  BigDecimal tongTien, String ghiChu) {
        this.idHoaDon = new SimpleIntegerProperty(idHoaDon);
        this.idNhanVien = new SimpleIntegerProperty(idNhanVien);
        this.ngayTao = new SimpleObjectProperty<>(ngayTao);
        this.tongTien = new SimpleObjectProperty<>(tongTien);
        this.ghiChu = new SimpleStringProperty(ghiChu);
        this.tenNhanVien = new SimpleStringProperty("");
    }

    // Constructor không có ID (dùng khi tạo hóa đơn mới)
    public HoaDon(int idNhanVien, String ghiChu) {
        this(0, idNhanVien, LocalDateTime.now(), BigDecimal.ZERO, ghiChu);
    }

    // Constructor mặc định
    public HoaDon() {
        this(0, 0, LocalDateTime.now(), BigDecimal.ZERO, "");
    }

    // ==================== GETTERS & SETTERS ====================

    // ID Hóa đơn
    public int getIdHoaDon() {
        return idHoaDon.get();
    }

    public void setIdHoaDon(int idHoaDon) {
        this.idHoaDon.set(idHoaDon);
    }

    public IntegerProperty idHoaDonProperty() {
        return idHoaDon;
    }

    // ID Nhân viên
    public int getIdNhanVien() {
        return idNhanVien.get();
    }

    public void setIdNhanVien(int idNhanVien) {
        this.idNhanVien.set(idNhanVien);
    }

    public IntegerProperty idNhanVienProperty() {
        return idNhanVien;
    }

    // Ngày tạo
    public LocalDateTime getNgayTao() {
        return ngayTao.get();
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao.set(ngayTao);
    }

    public ObjectProperty<LocalDateTime> ngayTaoProperty() {
        return ngayTao;
    }

    // Tổng tiền
    public BigDecimal getTongTien() {
        return tongTien.get();
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien.set(tongTien);
    }

    public ObjectProperty<BigDecimal> tongTienProperty() {
        return tongTien;
    }

    // Ghi chú
    public String getGhiChu() {
        return ghiChu.get();
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu.set(ghiChu);
    }

    public StringProperty ghiChuProperty() {
        return ghiChu;
    }

    // Tên nhân viên (để hiển thị)
    public String getTenNhanVien() {
        return tenNhanVien.get();
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien.set(tenNhanVien);
    }

    public StringProperty tenNhanVienProperty() {
        return tenNhanVien;
    }

    @Override
    public String toString() {
        return "HD" + idHoaDon.get() + " - " + tongTien.get() + " VNĐ";
    }
}
