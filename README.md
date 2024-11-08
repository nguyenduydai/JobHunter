 -- dự án website đăng tin tuyển dụng, có kết hợp phân quyền người dùng được xây dựng bằng backend với java spring boot và frontend với react --
- Các tinh năng :Ngoài các tính năng CRUD (thêm,sửa,xóa,hiển thị), dự án được chia thành các modules:
    + Module Users: đăng ký, đăng nhập người dùng theo Role (vai trò)
    + Module Permission (quyền hạn): người dùng được phân quyền để sử dụng hệ thống, ví dụ như user, admin...
    + Đối với từng Role và Permission, người dùng sẽ có giao diện hiển thị khác nhau
    + Module Company, Jobs: hiển thị thông tin về công ty và tin tuyển dụng
    + Module Subscribers: gửi email thông báo tự động với cron-job
- Cách chạy project :
    +Thay đổi cấu hình kết nối mysql ở file application.properties 
    + Chạy file dumpdata.sql ở mysql
    + Khởi chạy backend bằng extension spring boot dashboard
    + Khởi chạy frontend bằng câu lệnh npm i -> npm run build -> npm preview ở terminal

...👋...👀...🌱...💞️...📫... 😄...⚡...
