package com.example.qlquancoffe.models;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SanPham {
    // Enum cho trạng thái
    public enum TrangThai {
        ConHang, HetHang;

        @Override
        public String toString() {
            return this == ConHang ? "Còn hàng" : "Hết hàng";
        }
    }

    private final IntegerProperty idSanPham;
    private final StringProperty tenSanPham;
    private final ObjectProperty<BigDecimal> giaBan;
    private final IntegerProperty soLuongTonKho;
    private final StringProperty anhSanPham;
    private final IntegerProperty idDanhMuc;
    private final ObjectProperty<TrangThai> trangThai;
    private final ObjectProperty<LocalDateTime> ngayTao;
    private final ObjectProperty<LocalDateTime> ngayCapNhat;

    // Thêm property cho tên danh mục (để hiển thị trong TableView)
    private final StringProperty tenDanhMuc;

    // Constructor đầy đủ
    public SanPham(int idSanPham, String tenSanPham, BigDecimal giaBan,
                   int soLuongTonKho, String anhSanPham, int idDanhMuc,
                   TrangThai trangThai, LocalDateTime ngayTao,
                   LocalDateTime ngayCapNhat) {
        this.idSanPham = new SimpleIntegerProperty(idSanPham);
        this.tenSanPham = new SimpleStringProperty(tenSanPham);
        this.giaBan = new SimpleObjectProperty<>(giaBan);
        this.soLuongTonKho = new SimpleIntegerProperty(soLuongTonKho);
        this.anhSanPham = new SimpleStringProperty(anhSanPham);
        this.idDanhMuc = new SimpleIntegerProperty(idDanhMuc);
        this.trangThai = new SimpleObjectProperty<>(trangThai);
        this.ngayTao = new SimpleObjectProperty<>(ngayTao);
        this.ngayCapNhat = new SimpleObjectProperty<>(ngayCapNhat);
        this.tenDanhMuc = new SimpleStringProperty("");
    }

    // Constructor không có ID (dùng khi thêm mới)
    public SanPham(String tenSanPham, BigDecimal giaBan, int soLuongTonKho,
                   String anhSanPham, int idDanhMuc) {
        this(0, tenSanPham, giaBan, soLuongTonKho, anhSanPham, idDanhMuc,
                TrangThai.ConHang, LocalDateTime.now(), LocalDateTime.now());
    }

    // Constructor mặc định
    public SanPham() {
        this(0, "", BigDecimal.ZERO, 0, "", 0,
                TrangThai.ConHang, LocalDateTime.now(), LocalDateTime.now());
    }

    // ==================== GETTERS & SETTERS ====================

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

    // Tên sản phẩm
    public String getTenSanPham() {
        return tenSanPham.get();
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham.set(tenSanPham);
    }

    public StringProperty tenSanPhamProperty() {
        return tenSanPham;
    }

    // Giá bán
    public BigDecimal getGiaBan() {
        return giaBan.get();
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan.set(giaBan);
    }

    public ObjectProperty<BigDecimal> giaBanProperty() {
        return giaBan;
    }

    // Số lượng tồn kho
    public int getSoLuongTonKho() {
        return soLuongTonKho.get();
    }

    public void setSoLuongTonKho(int soLuongTonKho) {
        this.soLuongTonKho.set(soLuongTonKho);
    }

    public IntegerProperty soLuongTonKhoProperty() {
        return soLuongTonKho;
    }

    // Ảnh sản phẩm
    public String getAnhSanPham() {
        return anhSanPham.get();
    }

    public void setAnhSanPham(String anhSanPham) {
        this.anhSanPham.set(anhSanPham);
    }

    public StringProperty anhSanPhamProperty() {
        return anhSanPham;
    }

    // ID Danh mục
    public int getIdDanhMuc() {
        return idDanhMuc.get();
    }

    public void setIdDanhMuc(int idDanhMuc) {
        this.idDanhMuc.set(idDanhMuc);
    }

    public IntegerProperty idDanhMucProperty() {
        return idDanhMuc;
    }

    // Trạng thái
    public TrangThai getTrangThai() {
        return trangThai.get();
    }

    public void setTrangThai(TrangThai trangThai) {
        this.trangThai.set(trangThai);
    }

    public ObjectProperty<TrangThai> trangThaiProperty() {
        return trangThai;
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

    // Ngày cập nhật
    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat.get();
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat.set(ngayCapNhat);
    }

    public ObjectProperty<LocalDateTime> ngayCapNhatProperty() {
        return ngayCapNhat;
    }

    // Tên danh mục (để hiển thị)
    public String getTenDanhMuc() {
        return tenDanhMuc.get();
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc.set(tenDanhMuc);
    }

    public StringProperty tenDanhMucProperty() {
        return tenDanhMuc;
    }

    @Override
    public String toString() {
        return tenSanPham.get();
    }
}
