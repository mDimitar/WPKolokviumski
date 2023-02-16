package mk.ukim.finki.wp.kol2022.g2.service.impl;


import mk.ukim.finki.wp.kol2022.g2.model.Course;
import mk.ukim.finki.wp.kol2022.g2.model.Student;
import mk.ukim.finki.wp.kol2022.g2.model.StudentType;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidCourseIdException;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidStudentIdException;
import mk.ukim.finki.wp.kol2022.g2.repository.CourseRepository;
import mk.ukim.finki.wp.kol2022.g2.repository.StudentRepository;
import mk.ukim.finki.wp.kol2022.g2.service.StudentService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Student> listAll() {
        return this.studentRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        return this.studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
    }

    @Override
    public Student create(String name, String email, String password, StudentType type, List<Long> courseId, LocalDate enrollmentDate) {
        List<Course> courses = new ArrayList<>();
        for(Long idCourse: courseId){
            Course course = this.courseRepository.findById(idCourse).orElseThrow(InvalidCourseIdException::new);
            courses.add(course);
        }
        Student student = new Student(name, email, this.passwordEncoder.encode(password), type, courses, enrollmentDate);
        return this.studentRepository.save(student);
    }

    @Override
    public Student update(Long id, String name, String email, String password, StudentType type, List<Long> coursesId, LocalDate enrollmentDate) {
        Student student = this.findById(id);
        List<Course> courses = new ArrayList<>();
        for(Long idCourse: coursesId){
            Course course = this.courseRepository.findById(idCourse).orElseThrow(InvalidCourseIdException::new);
            courses.add(course);
        }
        student.setName(name);
        student.setEmail(email);
        student.setPassword(this.passwordEncoder.encode(password));
        student.setType(type);
        student.setCourses(courses);
        student.setEnrollmentDate(enrollmentDate);
        return this.studentRepository.save(student);
    }

    @Override
    public Student delete(Long id) {
        Student student = this.findById(id);
        this.studentRepository.delete(student);
        return student;
    }

    @Override
    public List<Student> filter(Long courseId, Integer yearsOfStudying) {
        if(courseId != null && yearsOfStudying != null){
            Course course = this.courseRepository.findById(courseId).orElseThrow(InvalidCourseIdException::new);
            return this.studentRepository.findAllByCoursesContainingAndEnrollmentDateBefore(course, LocalDate.now().minusYears(yearsOfStudying));
        }else if(courseId != null){
            Course course = this.courseRepository.findById(courseId).orElseThrow(InvalidCourseIdException::new);
            return this.studentRepository.findAllByCoursesContaining(course);
        }else if(yearsOfStudying != null){
            return this.studentRepository.findAllByEnrollmentDateBefore(LocalDate.now().minusYears(yearsOfStudying));
        }
        return this.listAll();
    }

}
