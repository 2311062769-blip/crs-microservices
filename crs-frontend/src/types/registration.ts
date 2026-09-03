export interface Registration {
  id: number;
  studentId: number;
  courseId: number;
  trangThai: string;
  ngayDangKy: string;
}

export interface RegistrationRequest {
  studentId: number;
  courseId: number;
}
