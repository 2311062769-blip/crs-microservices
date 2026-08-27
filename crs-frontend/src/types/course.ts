export interface Course {
  id: number;
  tenMonHoc: string;
  soTinChi: number;
  soChoConLai: number;
  soChoToiDa: number;
}

export interface CoursePageResponse {
  content: Course[];
  totalPages: number;
  totalElements: number;
  page: number;
  size: number;
}

export interface CourseFormValues {
  tenMonHoc: string;
  soTinChi: string;
  soChoToiDa: string;
}

export const emptyCourseForm: CourseFormValues = {
  tenMonHoc: '',
  soTinChi: '',
  soChoToiDa: '',
};
