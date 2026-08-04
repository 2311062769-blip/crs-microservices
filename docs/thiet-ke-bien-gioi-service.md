# Thiết kế biên giới Service

## 1. Danh sách Service

| Service | Cổng | Database | Trách nhiệm chính |
|----------|------|----------|-------------------|
| api-gateway | 8080 | Không có | Điểm vào duy nhất của hệ thống, định tuyến request, CORS, xác thực sơ bộ |
| auth-service | 8081 | auth_db | Quản lý User, Student, đăng nhập, xác thực JWT |
| course-service | 8082 | course_db | Quản lý Course, tìm kiếm, phân trang, quản lý số chỗ |
| registration-service | 8083 | registration_db | Quản lý đăng ký học phần, gọi sang course-service để đăng ký |

---

## 2. Nguyên tắc sở hữu dữ liệu (Data Ownership)

- Mỗi service có **database riêng**.
- Không service nào được truy cập trực tiếp database của service khác.
- Muốn lấy hoặc thay đổi dữ liệu của service khác phải gọi **REST API**.
- registration-service không có bảng Course.
- registration-service chỉ lưu `courseId`, không tạo khóa ngoại tới course_db.

---

## 3. Bảng định tuyến Gateway

| Route | Forward tới | Ghi chú |
|-------|-------------|----------|
| /api/auth/** | http://localhost:8081 | Login Public, các API khác cần JWT |
| /api/courses/** | http://localhost:8082 | GET Public, POST/PUT/DELETE cần ADMIN |
| /api/registrations/** | http://localhost:8083 | Cần JWT (STUDENT/ADMIN) |
| /api/public/courses | http://localhost:8082 | Dùng API Key cho đối tác ngoài |