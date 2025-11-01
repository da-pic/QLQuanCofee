package com.example.qlquancoffe;

import com.example.qlquancoffe.dao.DanhMucDAO;
import com.example.qlquancoffe.dao.HoaDonDAO;
import com.example.qlquancoffe.dao.SanPhamDAO;
import com.example.qlquancoffe.dao.TaiKhoanDAO;
import com.example.qlquancoffe.models.DanhMuc;
import com.example.qlquancoffe.models.SanPham;
import com.example.qlquancoffe.models.TaiKhoan;
import com.example.qlquancoffe.utils.PasswordUtil;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Test {
    public static void main(String[] args) {
        System.out.println("🧪 TEST TẤT CẢ DAO\n");

        // Test DanhMucDAO
        System.out.println("1️⃣ Test DanhMucDAO:");
        DanhMucDAO danhMucDAO = new DanhMucDAO();
        ObservableList<DanhMuc> danhMucs = danhMucDAO.getAllDanhMuc();
        System.out.println("   Số danh mục: " + danhMucs.size() + "\n");

        // Test TaiKhoanDAO
        System.out.println("2️⃣ Test TaiKhoanDAO:");
        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
        TaiKhoan tk = taiKhoanDAO.checkLogin("admin", "admin123");
        if (tk != null) {
            System.out.println("   ✅ Đăng nhập thành công: " + tk.getHoTen());
        }
        System.out.println();

        // Test SanPhamDAO
        System.out.println("3️⃣ Test SanPhamDAO:");
        SanPhamDAO sanPhamDAO = new SanPhamDAO();
        ObservableList<SanPham> sanPhams = sanPhamDAO.getAllSanPham();
        System.out.println("   Số sản phẩm: " + sanPhams.size() + "\n");

        // Test HoaDonDAO
        System.out.println("4️⃣ Test HoaDonDAO:");
        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        BigDecimal doanhThu = hoaDonDAO.getTongDoanhThuByDate(LocalDate.now());
        System.out.println("   Doanh thu hôm nay: " + doanhThu + "\n");

        System.out.println("✅ TEST HOÀN TẤT!");
    }
}
