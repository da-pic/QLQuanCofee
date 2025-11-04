package com.example.qlquancoffe.controllers;

import com.example.qlquancoffe.dao.DanhMucDAO;
import com.example.qlquancoffe.dao.SanPhamDAO;
import com.example.qlquancoffe.models.DanhMuc;
import com.example.qlquancoffe.models.SanPham;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;

public class SanPhamController {

    @FXML private TabPane tabPaneDanhMuc;
    @FXML private TextField txtTenSanPham, txtGiaBan, txtSoLuong, txtSearch, txtAnhSanPham;
    @FXML private Label lblDanhMucHienTai;

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();
    private ObservableList<DanhMuc> dsDanhMuc;

    // Lưu ID sản phẩm đang được chọn
    private Integer selectedSanPhamId = null;

    @FXML
    public void initialize() {
        loadTabsDanhMuc();
        capNhatDanhMucTheoTab();
    }

    /**
     * Tạo các Tab danh mục và hiển thị danh sách sản phẩm theo từng danh mục
     */
    private void loadTabsDanhMuc() {
        tabPaneDanhMuc.getTabs().clear();
        dsDanhMuc = danhMucDAO.getAllDanhMuc();
        selectedSanPhamId = null;

        for (DanhMuc dm : dsDanhMuc) {
            Tab tab = new Tab(dm.getTenDanhMuc());
            ScrollPane scroll = taoDanhSachSanPham(dm.getIdDanhMuc());
            tab.setContent(scroll);
            tabPaneDanhMuc.getTabs().add(tab);
        }

        // Khi người dùng đổi tab
        tabPaneDanhMuc.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                int index = tabPaneDanhMuc.getTabs().indexOf(newTab);
                if (index >= 0 && index < dsDanhMuc.size()) {
                    DanhMuc dm = dsDanhMuc.get(index);
                    lblDanhMucHienTai.setText(dm.getTenDanhMuc()); // ✅ Cập nhật Label danh mục
                    newTab.setContent(taoDanhSachSanPham(dm.getIdDanhMuc()));
                }
            }
        });

        // Ban đầu hiển thị danh mục đầu tiên
        if (!dsDanhMuc.isEmpty()) {
            lblDanhMucHienTai.setText(dsDanhMuc.get(0).getTenDanhMuc());
        }
    }

    /**
     * Đồng bộ Label danh mục theo tab hiện tại
     */
    private void capNhatDanhMucTheoTab() {
        if (tabPaneDanhMuc.getTabs().isEmpty()) {
            lblDanhMucHienTai.setText("Chưa có danh mục");
            return;
        }
        Tab currentTab = tabPaneDanhMuc.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            int index = tabPaneDanhMuc.getTabs().indexOf(currentTab);
            lblDanhMucHienTai.setText(dsDanhMuc.get(index).getTenDanhMuc());
        }
    }

    /**
     * Hiển thị danh sách sản phẩm dạng thẻ
     */
    private ScrollPane taoDanhSachSanPham(int idDanhMuc) {
        ObservableList<SanPham> dsSanPham = sanPhamDAO.getSanPhamByDanhMuc(idDanhMuc);

        FlowPane flow = new FlowPane();
        flow.setHgap(15);
        flow.setVgap(15);
        flow.setPadding(new Insets(10));
        flow.setPrefWrapLength(800);

        for (SanPham sp : dsSanPham) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/qlquancoffe/views/quanly/SanPhamItem.fxml"));
                HBox card = loader.load();
                SanPhamItemController itemCtrl = loader.getController();
                itemCtrl.setData(sp);

                // Click vào thẻ
                card.setOnMouseClicked(e -> {
                    selectedSanPhamId = sp.getIdSanPham();
                    txtTenSanPham.setText(sp.getTenSanPham());
                    txtGiaBan.setText(sp.getGiaBan().toString());
                    txtSoLuong.setText(String.valueOf(sp.getSoLuongTonKho()));
                    txtAnhSanPham.setText(sp.getAnhSanPham());
                });

                flow.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ScrollPane scroll = new ScrollPane(flow);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    // ===================== Tìm kiếm =====================
    @FXML
    private void onSearch() {
        String keyword = txtSearch.getText().trim();
        Tab currentTab = tabPaneDanhMuc.getSelectionModel().getSelectedItem();
        if (currentTab == null) return;

        int index = tabPaneDanhMuc.getTabs().indexOf(currentTab);
        int idDanhMuc = dsDanhMuc.get(index).getIdDanhMuc();

        ObservableList<SanPham> ketQua = keyword.isEmpty()
                ? sanPhamDAO.getSanPhamByDanhMuc(idDanhMuc)
                : sanPhamDAO.searchSanPham(keyword, idDanhMuc);

        FlowPane flow = new FlowPane(15, 15);
        flow.setPadding(new Insets(10));

        for (SanPham sp : ketQua) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/qlquancoffe/views/quanly/SanPhamItem.fxml"));
                HBox card = loader.load();
                SanPhamItemController itemCtrl = loader.getController();
                itemCtrl.setData(sp);

                card.setOnMouseClicked(e -> {
                    selectedSanPhamId = sp.getIdSanPham();
                    txtTenSanPham.setText(sp.getTenSanPham());
                    txtGiaBan.setText(sp.getGiaBan().toString());
                    txtSoLuong.setText(String.valueOf(sp.getSoLuongTonKho()));
                    txtAnhSanPham.setText(sp.getAnhSanPham());
                });

                flow.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ScrollPane scroll = new ScrollPane(flow);
        scroll.setFitToWidth(true);
        currentTab.setContent(scroll);
    }

    // ===================== Thêm =====================
    @FXML
    private void onAdd() {
        try {
            Tab tab = tabPaneDanhMuc.getSelectionModel().getSelectedItem();
            if (tab == null) {
                showAlert("Vui lòng chọn danh mục (tab)!", Alert.AlertType.WARNING);
                return;
            }

            int index = tabPaneDanhMuc.getTabs().indexOf(tab);
            int idDanhMuc = dsDanhMuc.get(index).getIdDanhMuc();

            SanPham sp = new SanPham(
                    txtTenSanPham.getText(),
                    new BigDecimal(txtGiaBan.getText()),
                    Integer.parseInt(txtSoLuong.getText()),
                    txtAnhSanPham.getText(),
                    idDanhMuc
            );

            if (sanPhamDAO.isTenSanPhamTonTai(sp.getTenSanPham())) {
                showAlert("Sản phẩm đã tồn tại!", Alert.AlertType.INFORMATION);
            } else if (sanPhamDAO.addSanPham(sp)) {
                showAlert("Thêm thành công!", Alert.AlertType.INFORMATION);
                selectedSanPhamId = sp.getIdSanPham();
                loadTabsDanhMuc();
            }
        } catch (Exception e) {
            showAlert("Lỗi nhập dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ===================== Sửa =====================
    @FXML
    private void onEdit() {
        try {
            if (selectedSanPhamId == null) {
                showAlert("Vui lòng chọn sản phẩm cần sửa!", Alert.AlertType.WARNING);
                return;
            }

            SanPham sp = sanPhamDAO.getSanPhamById(selectedSanPhamId);
            if (sp == null) {
                showAlert("Không tìm thấy sản phẩm!", Alert.AlertType.ERROR);
                return;
            }

            sp.setTenSanPham(txtTenSanPham.getText());
            sp.setGiaBan(new BigDecimal(txtGiaBan.getText()));
            sp.setSoLuongTonKho(Integer.parseInt(txtSoLuong.getText()));
            sp.setAnhSanPham(txtAnhSanPham.getText());

            if (sanPhamDAO.updateSanPham(sp)) {
                showAlert("Cập nhật thành công!", Alert.AlertType.INFORMATION);
                loadTabsDanhMuc();
            } else {
                showAlert("Cập nhật thất bại!", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            showAlert("Lỗi cập nhật: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ===================== Xóa =====================
    @FXML
    private void onDelete() {
        try {
            if (selectedSanPhamId == null) {
                showAlert("Vui lòng chọn sản phẩm cần xóa!", Alert.AlertType.WARNING);
                return;
            }

            SanPham sp = sanPhamDAO.getSanPhamById(selectedSanPhamId);
            if (sp == null) {
                showAlert("Không tìm thấy sản phẩm để xóa!", Alert.AlertType.ERROR);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Bạn có chắc chắn muốn xóa sản phẩm '" + sp.getTenSanPham() + "'?",
                    ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                if (sanPhamDAO.deleteSanPham(sp.getIdSanPham())) {
                    showAlert("Đã xóa sản phẩm!", Alert.AlertType.INFORMATION);
                    selectedSanPhamId = null;
                    loadTabsDanhMuc();
                } else {
                    showAlert("Xóa thất bại!", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            showAlert("Lỗi khi xóa: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ===================== Chọn ảnh =====================
    @FXML
    private void onChooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn ảnh sản phẩm");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.jpg", "*.png", "*.jpeg")
        );

        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            txtAnhSanPham.setText(file.getAbsolutePath());
        }
    }

    // ===================== Thông báo =====================
    private void showAlert(String msg, Alert.AlertType type) {
        new Alert(type, msg).showAndWait();
    }

    // Làm mới form
    @FXML
    private void onReload() {
        clearForm();
    }

    private void clearForm() {
        // Xóa toàn bộ nội dung ở các ô nhập liệu
        txtTenSanPham.clear();
        txtGiaBan.clear();
        txtSoLuong.clear();
        txtAnhSanPham.clear();
        txtSearch.clear();

        // Bỏ chọn sản phẩm hiện tại
        selectedSanPhamId = null;

        // Làm mới nội dung danh sách sản phẩm
        loadTabsDanhMuc();
    }
}

