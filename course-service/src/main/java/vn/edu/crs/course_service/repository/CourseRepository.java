package vn.edu.crs.course_service.repository;

import vn.edu.crs.course_service.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTenMonHocIgnoreCase(String tenMonHoc);

    // Buoi 3: Spring Data JPA tu sinh cau lenh SQL LIKE %keyword% khong phan biet hoa/thuong
    Page<Course> findByTenMonHocContainingIgnoreCase(String keyword, Pageable pageable);
}
