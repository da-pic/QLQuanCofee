package com.example.qlquancoffe.controllers;

import com.example.qlquancoffe.dao.TaiKhoanDAO;
import com.example.qlquancoffe.models.TaiKhoan;
import com.example.qlquancoffe.utils.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.util.Optional;

public class NhanVienController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TableView<TaiKhoan> tableTaiKhoan;
    @FXML
    private TableColumn<TaiKhoan, Number> colId;
    @FXML
    private TableColumn<TaiKhoan, String> colHoTen;
    @FXML
    private TableColumn<TaiKhoan, String> colTenDangNhap;
    @FXML
    private TableColumn<TaiKhoan, TaiKhoan.VaiTro> colVaiTro;
    @FXML
    private TableColumn<TaiKhoan, TaiKhoan.TrangThai> colTrangThai;
    @FXML
    private TableColumn<TaiKhoan, String> colNgayTao;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtHoTen;
    @FXML
    private TextField txtTenDangNhap;
    @FXML
    private TextField txtMatKhau;
    @FXML
    private ComboBox<TaiKhoan.VaiTro> cbVaiTro;
    @FXML
    private ComboBox<TaiKhoan.TrangThai> cbTrangThai;

    @FXML
    private Button btnThem;
    @FXML
    private Button btnSua;
    @FXML
    private Button btnXoa;
    @FXML
    private Button btnLamMoi;

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private ObservableList<TaiKhoan> danhSachTaiKhoan = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Gán giá trị cho ComboBox
        cbVaiTro.setItems(FXCollections.observableArrayList(TaiKhoan.VaiTro.values()));
        cbTrangThai.setItems(FXCollections.observableArrayList(TaiKhoan.TrangThai.values()));

        // Gán cột cho TableView
        colId.setCellValueFactory(data -> data.getValue().idNhanVienProperty());
        colHoTen.setCellValueFactory(data -> data.getValue().hoTenProperty());
        colTenDangNhap.setCellValueFactory(data -> data.getValue().tenDangNhapProperty());
        colVaiTro.setCellValueFactory(data -> data.getValue().vaiTroProperty());
        colTrangThai.setCellValueFactory(data -> data.getValue().trangThaiProperty());
        colNgayTao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNgayTao().toString()
                )
        );

        loadData();
        handleRowSelection();
    }

    private void loadData() {
        danhSachTaiKhoan = taiKhoanDAO.getAllTaiKhoan();
        tableTaiKhoan.setItems(danhSachTaiKhoan);
    }

    private void handleRowSelection() {
        tableTaiKhoan.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtHoTen.setText(newSel.getHoTen());
                txtTenDangNhap.setText(newSel.getTenDangNhap());
                cbVaiTro.setValue(newSel.getVaiTro());
                cbTrangThai.setValue(newSel.getTrangThai());
                txtMatKhau.setText(newSel.getMatKhau());
            }
        });
    }

    // Nút Thêm tài khoản
    @FXML
    private void onAdd() {
        String hoTen = txtHoTen.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText().trim();
        TaiKhoan.VaiTro vaiTro = cbVaiTro.getValue();

        if (hoTen.isEmpty() || tenDangNhap.isEmpty() || matKhau.isEmpty() || vaiTro == null) {
            showAlert("Vui lòng nhập đầy đủ thông tin.", Alert.AlertType.WARNING);
            return;
        }

        if (taiKhoanDAO.isUsernameExist(tenDangNhap)) {
            showAlert("Tên đăng nhập đã tồn tại!", Alert.AlertType.ERROR);
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(matKhau);
        TaiKhoan tk = new TaiKhoan(hoTen, tenDangNhap, hashedPassword, vaiTro);

        if (taiKhoanDAO.addTaiKhoan(tk)) {
            showAlert("Thêm tài khoản thành công!", Alert.AlertType.INFORMATION);
            loadData();
            clearForm();
        } else {
            showAlert("Thêm thất bại!", Alert.AlertType.ERROR);
        }
    }

    // Nút Sửa thông tin
    @FXML
    private void onEdit() {
        TaiKhoan selected = tableTaiKhoan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn tài khoản cần sửa.", Alert.AlertType.WARNING);
            return;
        }

        selected.setHoTen(txtHoTen.getText().trim());
        selected.setVaiTro(cbVaiTro.getValue());
        selected.setTrangThai(cbTrangThai.getValue());

        if (taiKhoanDAO.updateTaiKhoan(selected)) {
            showAlert("Cập nhật thành công!", Alert.AlertType.INFORMATION);
            loadData();
        } else {
            showAlert("Cập nhật thất bại!", Alert.AlertType.ERROR);
        }
    }

    // Nút Xóa (soft delete)
    @FXML
    private void onDelete() {
        TaiKhoan selected = tableTaiKhoan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn tài khoản cần xóa.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Xác nhận xóa tài khoản này?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Xóa tài khoản");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (taiKhoanDAO.deleteTaiKhoan(selected.getIdNhanVien())) {
                showAlert("Xóa thành công!", Alert.AlertType.INFORMATION);
                loadData();
                clearForm();
            } else {
                showAlert("Xóa thất bại!", Alert.AlertType.ERROR);
            }
        }
    }


    // Làm mới form
    @FXML
    private void onReload() {
        clearForm();
        loadData();
    }

    private void clearForm() {
        txtHoTen.clear();
        txtTenDangNhap.clear();
        txtMatKhau.clear();
        cbVaiTro.setValue(null);
        cbTrangThai.setValue(null);
        tableTaiKhoan.getSelectionModel().clearSelection();
    }

    // Tìm kiếm theo họ tên hoặc tên đăng nhập
    @FXML
    private void onSearch(KeyEvent event) {
        String keyword = txtSearch.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            tableTaiKhoan.setItems(danhSachTaiKhoan);
            return;
        }

        ObservableList<TaiKhoan> filtered = danhSachTaiKhoan.filtered(tk ->
                tk.getHoTen().toLowerCase().contains(keyword)
                        || tk.getTenDangNhap().toLowerCase().contains(keyword)
        );

        tableTaiKhoan.setItems(filtered);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
