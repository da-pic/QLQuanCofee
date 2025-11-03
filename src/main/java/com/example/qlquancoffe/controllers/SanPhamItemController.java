package com.example.qlquancoffe.controllers;

import com.example.qlquancoffe.models.SanPham;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SanPhamItemController {

    @FXML private ImageView imgSanPham;
    @FXML private Label lblTenSanPham;
    @FXML private Label lblGiaBan;
    @FXML private Label lblDanhMuc;
    @FXML private Label lblSoLuong;
    @FXML private Label lblTrangThai;

    public void setData(SanPham sp) {
        lblTenSanPham.setText(sp.getTenSanPham());
        lblGiaBan.setText(String.format("%,.0f VNĐ", sp.getGiaBan()));
        lblDanhMuc.setText("Danh mục: " + sp.getTenDanhMuc());
        lblSoLuong.setText("Tồn kho: " + sp.getSoLuongTonKho());
        lblTrangThai.setText("Trạng thái: " + sp.getTrangThai());

        try {
            if (sp.getAnhSanPham() != null && !sp.getAnhSanPham().isEmpty()) {
                imgSanPham.setImage(new Image("file:" + sp.getAnhSanPham()));
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi ảnh sản phẩm: " + e.getMessage());
        }
    }
}