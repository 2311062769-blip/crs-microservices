# Blueprint API

## Auth Service

Cổng: **8081**

Tiền tố qua Gateway:

```
/api/auth
```

| Method | Endpoint | Mô tả | Yêu cầu |
|---------|----------|--------|----------|
| POST | /auth/login | Đăng nhập, trả JWT | Public |
| POST | /auth/register | Đăng ký tài khoản | Public |

---

## Course Service

Cổng: **8082**

Tiền tố:

```
/api/courses
```

| Method | Endpoint | Mô tả | Quyền |
|---------|----------|------|--------|
| GET | /courses | Danh sách môn học, tìm kiếm, phân trang | Public |
| GET | /courses/{id} | Chi tiết môn học | Public |
| POST | /courses | Thêm môn học | ADMIN |
| PUT | /courses/{id} | Cập nhật môn học | ADMIN |
| DELETE | /courses/{id} | Xóa môn học | ADMIN |

### API nội bộ

Không public qua Gateway.

| Method | Endpoint | Mô tả |
|---------|----------|------|
| PATCH | /internal/courses/{id}/reserve-seat | Kiểm tra còn chỗ và trừ số chỗ |
| PATCH | /internal/courses/{id}/release-seat | Hoàn trả số chỗ khi hủy đăng ký |

---

## Registration Service

Cổng: **8083**

Tiền tố:

```
/api/registrations
```

| Method | Endpoint | Mô tả | Quyền |
|---------|----------|------|--------|
| POST | /registrations | Đăng ký học phần | STUDENT |
| GET | /registrations/my | Danh sách đăng ký của tôi | STUDENT |
| DELETE | /registrations/{id} | Hủy đăng ký học phần | STUDENT / ADMIN |