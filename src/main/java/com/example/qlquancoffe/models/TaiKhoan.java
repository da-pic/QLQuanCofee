package com.example.qlquancoffe.models;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class TaiKhoan {
    // Enum cho vai trò
    public enum VaiTro {
        QuanLy, NhanVien;

        @Override
        public String toString() {
            return this == QuanLy ? "Quản lý" : "Nhân viên";
        }
    }

    // Enum cho trạng thái
    public enum TrangThai {
        DangLamViec, DaNghiViec;

        @Override
        public String toString() {
            return this == DangLamViec ? "Đang làm việc" : "Đã nghỉ việc";
        }
    }

    private final IntegerProperty idNhanVien;
    private final StringProperty hoTen;
    private final StringProperty tenDangNhap;
    private final StringProperty matKhau;
    private final ObjectProperty<VaiTro> vaiTro;
    private final ObjectProperty<TrangThai> trangThai;
    private final ObjectProperty<LocalDateTime> ngayTao;

    // Constructor đầy đủ
    public TaiKhoan(int idNhanVien, String hoTen, String tenDangNhap,
                    String matKhau, VaiTro vaiTro, TrangThai trangThai,
                    LocalDateTime ngayTao) {
        this.idNhanVien = new SimpleIntegerProperty(idNhanVien);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.tenDangNhap = new SimpleStringProperty(tenDangNhap);
        this.matKhau = new SimpleStringProperty(matKhau);
        this.vaiTro = new SimpleObjectProperty<>(vaiTro);
        this.trangThai = new SimpleObjectProperty<>(trangThai);
        this.ngayTao = new SimpleObjectProperty<>(ngayTao);
    }

    // Constructor không có ID (dùng khi thêm mới)
    public TaiKhoan(String hoTen, String tenDangNhap, String matKhau, VaiTro vaiTro) {
        this(0, hoTen, tenDangNhap, matKhau, vaiTro,
                TrangThai.DangLamViec, LocalDateTime.now());
    }

    // Constructor mặc định
    public TaiKhoan() {
        this(0, "", "", "", VaiTro.NhanVien,
                TrangThai.DangLamViec, LocalDateTime.now());
    }

    // ==================== GETTERS & SETTERS ====================

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

    // Họ tên
    public String getHoTen() {
        return hoTen.get();
    }

    public void setHoTen(String hoTen) {
        this.hoTen.set(hoTen);
    }

    public StringProperty hoTenProperty() {
        return hoTen;
    }

    // Tên đăng nhập
    public String getTenDangNhap() {
        return tenDangNhap.get();
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap.set(tenDangNhap);
    }

    public StringProperty tenDangNhapProperty() {
        return tenDangNhap;
    }

    // Mật khẩu
    public String getMatKhau() {
        return matKhau.get();
    }

    public void setMatKhau(String matKhau) {
        this.matKhau.set(matKhau);
    }

    public StringProperty matKhauProperty() {
        return matKhau;
    }

    // Vai trò
    public VaiTro getVaiTro() {
        return vaiTro.get();
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro.set(vaiTro);
    }

    public ObjectProperty<VaiTro> vaiTroProperty() {
        return vaiTro;
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

    @Override
    public String toString() {
        return hoTen.get() + " (" + tenDangNhap.get() + ")";
    }
}
