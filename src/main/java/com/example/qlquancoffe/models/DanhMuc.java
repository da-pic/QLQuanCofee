package com.example.qlquancoffe.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DanhMuc {
    private final IntegerProperty idDanhMuc;
    private final StringProperty tenDanhMuc;

    // Constructor đầy đủ
    public DanhMuc(int idDanhMuc, String tenDanhMuc) {
        this.idDanhMuc = new SimpleIntegerProperty(idDanhMuc);
        this.tenDanhMuc = new SimpleStringProperty(tenDanhMuc);
    }

    // Constructor không có ID (dùng khi thêm mới)
    public DanhMuc(String tenDanhMuc) {
        this(0, tenDanhMuc);
    }

    // Constructor mặc định
    public DanhMuc() {
        this(0, "");
    }

    // Getters và Setters cho ID
    public int getIdDanhMuc() {
        return idDanhMuc.get();
    }

    public void setIdDanhMuc(int idDanhMuc) {
        this.idDanhMuc.set(idDanhMuc);
    }

    public IntegerProperty idDanhMucProperty() {
        return idDanhMuc;
    }

    // Getters và Setters cho Tên danh mục
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
        return tenDanhMuc.get(); // Hiển thị trong ComboBox
    }
}
