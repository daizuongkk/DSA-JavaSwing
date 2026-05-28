# Hệ thống chữ ký số DSA bằng Java Swing

Ứng dụng minh họa chữ ký số DSA và xác thực tính toàn vẹn dữ liệu. Dự án được tách lớp để dễ đọc, dễ bảo trì và đúng hơn với mô hình một hệ thống hoàn chỉnh.

## Chức năng

- Tạo cặp khóa DSA 2048 bit.
- Ký văn bản hoặc tệp tin bằng `SHA256withDSA`.
- Xác minh chữ ký bằng khóa công khai.
- Phát hiện dữ liệu bị thay đổi sau khi ký.
- Tính hash SHA-256 để đối chiếu tính toàn vẹn.
- Lưu/mở khóa và chữ ký dạng Base64.

## Cấu trúc source

```text
src/
  App.java                    Điểm khởi động chương trình
  crypto/
    DsaService.java           Sinh khóa, ký, xác minh, hash
  model/
    DataMode.java             Chế độ xử lý văn bản/tệp tin
    DsaKeyPairText.java       Model cặp khóa dạng Base64
  ui/
    MainFrame.java            Giao diện chính và điều phối sự kiện
    Theme.java                Màu sắc, font, look and feel
    UiFactory.java            Factory tạo component Swing dùng chung
  util/
    EncodingUtils.java        Base64, hex
    KeyFileFormatter.java     Định dạng lưu/mở tệp khóa
```

## Cách chạy

Biên dịch tất cả source:

```powershell
$files = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -encoding UTF-8 --release 21 -d bin $files
```

Chạy ứng dụng:

```powershell
java -cp bin App
```

Nếu chạy bằng Java 21, hãy biên dịch với `--release 21` như lệnh trên. Nếu không, class được tạo bằng Java mới hơn có thể gây lỗi `UnsupportedClassVersionError`.

Trong VS Code, có thể mở `src/App.java` và bấm `Run`.

## Quy trình demo

1. Bấm `Tạo khóa` để sinh khóa bí mật và khóa công khai.
2. Nhập nội dung văn bản, hoặc chọn chế độ `Tệp tin` và bấm `Chọn tệp`.
3. Bấm `Ký dữ liệu` để tạo chữ ký DSA Base64.
4. Bấm `Xác minh` để kiểm tra tính toàn vẹn.
5. Sửa nội dung/tệp tin rồi xác minh lại. Ứng dụng sẽ báo chữ ký không hợp lệ.
