package com.example.qlquancoffe.models;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class ChiTietHoaDon {
    private final IntegerProperty idHoaDon;
    private final IntegerProperty idSanPham;
    private final IntegerProperty soLuong;
    private final ObjectProperty<BigDecimal> donGia;
    private final ObjectProperty<BigDecimal> thanhTien;

    // Thêm property cho tên sản phẩm (để hiển thị)
    private final StringProperty tenSanPham;

    // Constructor đầy đủ
    public ChiTietHoaDon(int idHoaDon, int idSanPham, int soLuong,
                         BigDecimal donGia, BigDecimal thanhTien) {
        this.idHoaDon = new SimpleIntegerProperty(idHoaDon);
        this.idSanPham = new SimpleIntegerProperty(idSanPham);
        this.soLuong = new SimpleIntegerProperty(soLuong);
        this.donGia = new SimpleObjectProperty<>(donGia);
        this.thanhTien = new SimpleObjectProperty<>(thanhTien);
        this.tenSanPham = new SimpleStringProperty("");
    }

    // Constructor tự tính thành tiền
    public ChiTietHoaDon(int idHoaDon, int idSanPham, int soLuong, BigDecimal donGia) {
        this(idHoaDon, idSanPham, soLuong, donGia,
                donGia.multiply(BigDecimal.valueOf(soLuong)));
    }

    // Constructor mặc định
    public ChiTietHoaDon() {
        this(0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO);
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

    // ID Sản phẩm
    public int getIdSanPham() {
        return idSanPham.get();
    }

    public void setIdSanPham(int idSanPham) {
        this.idSanPham.set(idSanPham);
    }

    public IntegerProperty idSanPhamProperty() {
        return idSanPham;
    }

    // Số lượng
    public int getSoLuong() {
        return soLuong.get();
    }

    public void setSoLuong(int soLuong) {
        this.soLuong.set(soLuong);
        // Tự động tính lại thành tiền
        this.thanhTien.set(donGia.get().multiply(BigDecimal.valueOf(soLuong)));
    }

    public IntegerProperty soLuongProperty() {
        return soLuong;
    }

    // Đơn giá
    public BigDecimal getDonGia() {
        return donGia.get();
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia.set(donGia);
        // Tự động tính lại thành tiền
        this.thanhTien.set(donGia.multiply(BigDecimal.valueOf(soLuong.get())));
    }

    public ObjectProperty<BigDecimal> donGiaProperty() {
        return donGia;
    }

    // Thành tiền
    public BigDecimal getThanhTien() {
        return thanhTien.get();
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien.set(thanhTien);
    }

    public ObjectProperty<BigDecimal> thanhTienProperty() {
        return thanhTien;
    }

    // Tên sản phẩm (để hiển thị)
    public String getTenSanPham() {
        return tenSanPham.get();
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham.set(tenSanPham);
    }

    public StringProperty tenSanPhamProperty() {
        return tenSanPham;
    }

    @Override
    public String toString() {
        return tenSanPham.get() + " x" + soLuong.get();
    }
}
