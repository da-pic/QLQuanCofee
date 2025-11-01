package com.example.qlquancoffe;

import com.example.qlquancoffe.dao.DanhMucDAO;
import com.example.qlquancoffe.models.DanhMuc;
import com.example.qlquancoffe.utils.DatabaseConnection;
import javafx.beans.Observable;
import javafx.collections.ObservableList;

public class App {
    public static void main(String[] args) {
        DanhMucDAO dao = new DanhMucDAO();
        System.out.println("Đang lấy danh mục");

        ObservableList<DanhMuc> list = dao.getAllDanhMuc();

        for (DanhMuc danhMuc : list) {
            System.out.println("-" + danhMuc.getTenDanhMuc());
        }
    }
}
