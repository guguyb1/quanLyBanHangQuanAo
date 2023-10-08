package com.quanlybanhangquanao.quanlybanhangquanao;

import com.quanlybanhangquanao.quanlybanhangquanao.models.SanPham;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuanLySanPhamViewController {

    @FXML
    private VBox ListSanPham; // Tham chiếu đến VBox trong QuanLySanPham.fxml

    @FXML
    private TextField inputTimKiemSanPham;
    @FXML
    private ItemListSanPhamController itemListController;

    @FXML
    private Pane mainPane;
    @FXML
    private Pane subPane;

    private SanPham sanPham;

    DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    @FXML
    private void initialize() {
        sanPham = new SanPham();
        inputTimKiemSanPham.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ListSanPham.getChildren().clear();
                loadSanPham(sanPham.TimKiem(newValue));
                System.out.println("Dữ liệu đã thay đổi thành: " + newValue);
                // Đây bạn có thể thực hiện xử lý dựa trên dữ liệu đã nhập
            }
        });

        loadSanPham(sanPham.DanhSach());
    }



    void loadSanPham(List<SanPham> danhSachSanPham){
        try {
            // Vòng lặp để nạp và thêm từng mục vào VBox
            for (int i = 0; i < danhSachSanPham.size(); i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemListSanPham.fxml"));
                HBox item = loader.load(); // Nạp ItemListSanPh am.fxml
                // Cài đặt dữ liệu cho mục
                ItemListSanPhamController itemController = loader.getController();
                itemController.setQuanLySanPhamController(this);
                itemController.setProductData(danhSachSanPham.get(i).getMaHang(), danhSachSanPham.get(i).getTenHang(), decimalFormat.format(danhSachSanPham.get(i).getGiaBan()), decimalFormat.format(danhSachSanPham.get(i).getGiaVon()),String.valueOf(danhSachSanPham.get(i).getTonKho()), String.valueOf(danhSachSanPham.get(i).getSoLuongDaBan()));

                ListSanPham.getChildren().add(item); // Thêm vào VBox
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleIconClick(String id, String typeButton) {
        if (typeButton.equals("edit")) {
            SanPham sanPham = new SanPham();
            loadScreen("ChiTietSanPham.fxml", subPane, typeButton, sanPham.ChiTiet(id));
            subPane.toFront();

        } else if (typeButton.equals("view")) {
            SanPham sanPham = new SanPham();
            loadScreen("ChiTietSanPham.fxml", subPane, typeButton, sanPham.ChiTiet(id));
            subPane.toFront();

        } else if (typeButton.equals("delete")) {
//            SanPham sanPham = new SanPham();
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Xác nhận");
            confirmationAlert.setHeaderText("Xác nhận xóa tệp?");
            confirmationAlert.setContentText("Bạn có chắc chắn muốn xóa tệp này?");
            ButtonType buttonTypeOK = new ButtonType("Xác nhận");
            ButtonType buttonTypeCancel = new ButtonType("Hủy");
            confirmationAlert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeOK) {
                    sanPham.Xoa(id);
                    ListSanPham.getChildren().clear();
                    loadSanPham(sanPham.DanhSach());
                    System.out.println("Xóa tệp thành công");

                } else {
                    System.out.println("Xóa tệp đã bị hủy.");
                }
            });
        } else if (typeButton.equals("print")) {

            double pageWidth = 794; // Width of the page in your desired unit (e.g., pixels)
            double pageHeight = 1123; // Height of the page in your desired unit (e.g., pixels)
            System.out.println(ListSanPham.getBoundsInParent().getWidth());
            System.out.println(ListSanPham.getBoundsInParent().getHeight());
            double scaleX = pageWidth / ListSanPham.getBoundsInParent().getWidth();
            double scaleY = pageHeight / ListSanPham.getBoundsInParent().getHeight();
            double scale = Math.min(scaleX, scaleY);

            VBox item = new VBox(ListSanPham);
            item.getTransforms().add(new Scale(scale, scale));

            PageLayout pageLayout = Printer.getDefaultPrinter().createPageLayout(
                    Paper.A4, PageOrientation.PORTRAIT, 0, 0, 0, 0
            );
            PrinterJob printerJob = PrinterJob.createPrinterJob();

            if (printerJob != null) {

                printerJob.getJobSettings().setPageLayout(pageLayout);

                // Print the scaled content
                boolean success = printerJob.printPage(item);
                if (success) {
                    System.out.println("Printing successful.");
                    printerJob.endJob();
                } else {
                    System.out.println("Printing failed.");
                }

            }
        }

    }
    @FXML
   void handleBtnThemClick(){
        loadScreen("ChiTietSanPham.fxml", subPane, "themSanPham",null);
        subPane.toFront();
    }

    public void handleChiTietSanPhamClick(String typeButton) {
        if (typeButton.equals("BtnQuayLai")) {

            subPane.getChildren().clear();
            ListSanPham.getChildren().clear();
            loadSanPham(sanPham.DanhSach());
            mainPane.toFront();
        }
    }


    private void loadScreen(String fxmlFileName, Pane container, String TypeButton, SanPham sanpham) {

        try {
            // Tải màn hình con từ tệp FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Pane screen = loader.load();
            ChiTietSanPhamController itemController = loader.getController();
            itemController.setQuanLySanPhamController(this);

            if (TypeButton.equals("edit")) {
                itemController.setTextButtonThem("Lưu", "submitEdit");
                itemController.setDataSanPham(sanpham.getMaHang(), sanpham.getTenHang(), decimalFormat.format(sanpham.getGiaBan()),  decimalFormat.format(sanpham.getGiaVon()),  sanpham.getNhomHang(), sanpham.getThuongHieu(), String.valueOf(sanpham.getTonKho()), String.valueOf(sanpham.getTrongLuong()));

            } else if (TypeButton.equals("view")) {
                itemController.setTextButtonThem("Sửa sản phẩm", "view");
                itemController.disableTextFieldEditing();
                //set data
                itemController.setDataSanPham(sanpham.getMaHang(), sanpham.getTenHang(), decimalFormat.format(sanpham.getGiaBan()),  decimalFormat.format(sanpham.getGiaVon()),  sanpham.getNhomHang(), sanpham.getThuongHieu(), String.valueOf(sanpham.getTonKho()), String.valueOf(sanpham.getTrongLuong()));
            }
            container.getChildren().clear();
            container.getChildren().add(screen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
