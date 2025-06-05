package com.sistema.gestion.Auth.Services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistema.gestion.Auth.Utils.JwtUtil;
import com.sistema.gestion.DTO.UserCredentialsDTO;
import com.sistema.gestion.DTO.UserInfo;
import com.sistema.gestion.Exceptions.UserNotFoundException;
import com.sistema.gestion.Models.Profiles.Parent;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Models.Profiles.Teacher;
import com.sistema.gestion.Models.Profiles.User;
import com.sistema.gestion.Repositories.Profiles.ParentsRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;
import com.sistema.gestion.Repositories.Profiles.TeacherRepository;
import com.sistema.gestion.Repositories.Profiles.UserRepository;
import com.sistema.gestion.Services.Profiles.StudentService;
import com.sistema.gestion.Services.Profiles.UserService;

import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor

 public class AuthService {

	private final UserRepository userRepository;
	private final ParentsRepository parentRepository;
    private final StudentService studentService;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final UserService userService;
    
    // Métodos para registrar diferentes tipos de usuarios
    public Mono<User> registerUser(UserInfo userDetails, String username) {
        return userService.getFullName(username)
                .flatMap(name -> {
                    User user = new User();
                    user.setEmail(userDetails.getUser().getEmail());
                    user.setPassword(passwordEncoder.encode(userDetails.getUser().getPassword()));
                    user.setName(userDetails.getUser().getName());
                    user.setSurname(userDetails.getUser().getSurname());
                    user.setPhone(userDetails.getUser().getPhone());
                    user.setInstitution(name.getInstitution());
                    user.setRoles(userDetails.getUser().getRoles());
                    user.setDni(userDetails.getUser().getDni());
                    user.setCreatedBy(name.getName()+ " " + name.getSurname());
                    user.setCreatedAt(LocalDateTime.now());
                    user.setModifiedBy("");
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .onErrorMap(e -> new RuntimeException("Error al registrar el usuario"));
    }

	public Mono<Parent> registerParent(UserInfo userDetails, String username) {
        return userService.getFullName(username)
                .flatMap(name -> {
                    Parent parent = new Parent();
                    parent.setEmail(userDetails.getParent().getEmail());
                    parent.setPassword(passwordEncoder.encode(userDetails.getParent().getPassword()));
                    parent.setName(userDetails.getParent().getName());
                    parent.setSurname(userDetails.getParent().getSurname());
                    parent.setDni(userDetails.getParent().getDni());
                    parent.setInstitution(name.getInstitution());
                    parent.setRoles(userDetails.getParent().getRoles());
                    parent.setPhone(userDetails.getParent().getPhone());
                    parent.setCreatedBy(name.getName()+ " " + name.getSurname());
                    parent.setCreatedAt(LocalDateTime.now());
                    parent.setModifiedBy("");
                    parent.setUpdatedAt(LocalDateTime.now());
                    return parentRepository.save(parent);
                })
                .onErrorMap(e -> new RuntimeException("Error al registrar el usuario"));
    }
    
    public Mono<Student> registerStudent(UserInfo userDetails, String username) {
        return userService.getFullName(username)
                .flatMap(name -> {
                    Student student = new Student();
                    student.setEmail(userDetails.getStudent().getEmail());
                    student.setPassword(passwordEncoder.encode(userDetails.getStudent().getPassword()));
                    student.setName(userDetails.getStudent().getName());
                    student.setSurname(userDetails.getStudent().getSurname());
                    student.setPhone(userDetails.getStudent().getPhone());
                    student.setStatus(userDetails.getStudent().getStatus());
                    student.setDni(userDetails.getStudent().getDni());
                    student.setCursesIds(userDetails.getStudent().getCursesIds());
                    student.setParentId(userDetails.getStudent().getParentId());
                    student.setInstitution(name.getInstitution());
                    student.setRoles(userDetails.getStudent().getRoles());
                    student.setCreatedBy(name.getName()+ " " + name.getSurname());
                    student.setCreatedAt(LocalDateTime.now());
                    student.setModifiedBy("");
                    student.setUpdatedAt(LocalDateTime.now());
                    return studentRepository.save(student)
                        .flatMap(savedStudent -> studentService.enrollStudentInCourses(savedStudent))
                        .flatMap(savedStudent -> studentService.enrollStudentInParents(savedStudent));
                })
                
                .onErrorMap(e -> new RuntimeException("Error al registrar el estudiante"));
    }

    public Mono<Teacher> registerTeacher(UserInfo userDetails, String username) {
        return userService.getFullName(username)
                .flatMap(name -> {
                    Teacher teacher = new Teacher();
                    teacher.setEmail(userDetails.getTeacher().getEmail());
                    teacher.setPassword(passwordEncoder.encode(userDetails.getTeacher().getPassword()));
                    teacher.setName(userDetails.getTeacher().getName());
                    teacher.setSurname(userDetails.getTeacher().getSurname());
                    teacher.setPhone(userDetails.getTeacher().getPhone());
                    teacher.setDni(userDetails.getTeacher().getDni());
                    teacher.setInstitution(name.getInstitution());
                    teacher.setRoles(userDetails.getTeacher().getRoles());
                    teacher.setCreatedBy(name.getName()+ " " + name.getSurname());
                    teacher.setCreatedAt(LocalDateTime.now());
                    teacher.setModifiedBy("");
                    teacher.setUpdatedAt(LocalDateTime.now());
                    return teacherRepository.save(teacher);
                })
                .onErrorMap(e -> new RuntimeException("Error al registrar el profesor"));
    }

    public Mono<User> updateUserProfile(UserInfo userDetails, String username) {
        return userService.getFullName(username)
            .flatMap(name -> userRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(existingUser -> {
                    existingUser.setEmail(userDetails.getUser().getEmail() != null && !userDetails.getUser().getEmail().isEmpty() ? userDetails.getUser().getEmail() : existingUser.getEmail());
                    existingUser.setPassword(userDetails.getUser().getPassword() != null && !userDetails.getUser().getPassword().isEmpty() ? passwordEncoder.encode(userDetails.getUser().getPassword()) : existingUser.getPassword());
                    existingUser.setName(userDetails.getUser().getName() != null && !userDetails.getUser().getName().isEmpty() ? userDetails.getUser().getName() : existingUser.getName());
                    existingUser.setSurname(userDetails.getUser().getSurname() != null && !userDetails.getUser().getSurname().isEmpty() ? userDetails.getUser().getSurname() : existingUser.getSurname());
                    existingUser.setDni(userDetails.getUser().getDni() != null && !userDetails.getUser().getDni().isEmpty() ? userDetails.getUser().getDni() : existingUser.getDni());
                    existingUser.setPhone(userDetails.getUser().getPhone() != null && !userDetails.getUser().getPhone().isEmpty() ? userDetails.getUser().getPhone() : existingUser.getPhone());
    
                    // Auditoría
                    existingUser.setModifiedBy(name.getName() + " " + name.getSurname());
                    existingUser.setUpdatedAt(LocalDateTime.now());
    
                    return userRepository.save(existingUser);
                })
            )
            .onErrorMap(e -> new RuntimeException("Error al actualizar el perfil del usuario", e));
    }

    public Mono<Parent> updateParentProfile(UserInfo userDetails, String username) {
        return userService.getFullName(username)
            .flatMap(name -> parentRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Padre no encontrado")))
                .flatMap(existingParent -> {
                    existingParent.setEmail(userDetails.getParent().getEmail() != null && !userDetails.getParent().getEmail().isEmpty() ? userDetails.getParent().getEmail() : existingParent.getEmail());
                    existingParent.setPassword(userDetails.getParent().getPassword() != null && !userDetails.getParent().getPassword().isEmpty() ? passwordEncoder.encode(userDetails.getParent().getPassword()) : existingParent.getPassword());
                    existingParent.setName(userDetails.getParent().getName() != null && !userDetails.getParent().getName().isEmpty() ? userDetails.getParent().getName() : existingParent.getName());
                    existingParent.setSurname(userDetails.getParent().getSurname() != null && !userDetails.getParent().getSurname().isEmpty() ? userDetails.getParent().getSurname() : existingParent.getSurname());
                    existingParent.setDni(userDetails.getParent().getDni() != null && !userDetails.getParent().getDni().isEmpty() ? userDetails.getParent().getDni() : existingParent.getDni());
                    existingParent.setPhone(userDetails.getParent().getPhone() != null && !userDetails.getParent().getPhone().isEmpty() ? userDetails.getParent().getPhone() : existingParent.getPhone());
    
                    // Auditoría
                    existingParent.setModifiedBy(name.getName() + " " + name.getSurname());
                    existingParent.setUpdatedAt(LocalDateTime.now());
    
                    return parentRepository.save(existingParent);
                })
            )
            .onErrorMap(e -> new RuntimeException("Error al actualizar el perfil del padre", e));
    }
    
    public Mono<Teacher> updateTeacherProfile(UserInfo userDetails, String username) {
        return userService.getFullName(username)
            .flatMap(name -> teacherRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Profesor no encontrado")))
                .flatMap(existingTeacher -> {
                    existingTeacher.setEmail(userDetails.getTeacher().getEmail() != null && !userDetails.getTeacher().getEmail().isEmpty() ? userDetails.getTeacher().getEmail() : existingTeacher.getEmail());
                    existingTeacher.setPassword(userDetails.getTeacher().getPassword() != null && !userDetails.getTeacher().getPassword().isEmpty() ? passwordEncoder.encode(userDetails.getTeacher().getPassword()) : existingTeacher.getPassword());
                    existingTeacher.setName(userDetails.getTeacher().getName() != null && !userDetails.getTeacher().getName().isEmpty() ? userDetails.getTeacher().getName() : existingTeacher.getName());
                    existingTeacher.setSurname(userDetails.getTeacher().getSurname() != null && !userDetails.getTeacher().getSurname().isEmpty() ? userDetails.getTeacher().getSurname() : existingTeacher.getSurname());
                    existingTeacher.setDni(userDetails.getTeacher().getDni() != null && !userDetails.getTeacher().getDni().isEmpty() ? userDetails.getTeacher().getDni() : existingTeacher.getDni());
                    existingTeacher.setPhone(userDetails.getTeacher().getPhone() != null && !userDetails.getTeacher().getPhone().isEmpty() ? userDetails.getTeacher().getPhone() : existingTeacher.getPhone());
                    existingTeacher.setInstitution(userDetails.getTeacher().getInstitution() != null && !userDetails.getTeacher().getInstitution().isEmpty() ? userDetails.getTeacher().getInstitution() : existingTeacher.getInstitution());
    
                    // Auditoría
                    existingTeacher.setModifiedBy(name.getName() + " " + name.getSurname());
                    existingTeacher.setUpdatedAt(LocalDateTime.now());
    
                    return teacherRepository.save(existingTeacher);
                })
            )
            .onErrorMap(e -> new RuntimeException("Error al actualizar el perfil del profesor", e));
    }
    
    public Mono<Student> updateStudentProfile(UserInfo userDetails, String username) {
        return userService.getFullName(username)
            .flatMap(name -> studentRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Estudiante no encontrado")))
                .flatMap(existingStudent -> {
                    existingStudent.setEmail(userDetails.getStudent().getEmail() != null && !userDetails.getStudent().getEmail().isEmpty() ? userDetails.getStudent().getEmail() : existingStudent.getEmail());
                    existingStudent.setPassword(userDetails.getStudent().getPassword() != null && !userDetails.getStudent().getPassword().isEmpty() ? passwordEncoder.encode(userDetails.getStudent().getPassword()) : existingStudent.getPassword());
                    existingStudent.setName(userDetails.getStudent().getName() != null && !userDetails.getStudent().getName().isEmpty() ? userDetails.getStudent().getName() : existingStudent.getName());
                    existingStudent.setCursesIds(userDetails.getStudent().getCursesIds() != null && !userDetails.getStudent().getCursesIds().isEmpty() ? userDetails.getStudent().getCursesIds() : existingStudent.getCursesIds());
                    existingStudent.setSurname(userDetails.getStudent().getSurname() != null && !userDetails.getStudent().getSurname().isEmpty() ? userDetails.getStudent().getSurname() : existingStudent.getSurname());
                    existingStudent.setDni(userDetails.getStudent().getDni() != null && !userDetails.getStudent().getDni().isEmpty() ? userDetails.getStudent().getDni() : existingStudent.getDni());
                    existingStudent.setPhone(userDetails.getStudent().getPhone() != null && !userDetails.getStudent().getPhone().isEmpty() ? userDetails.getStudent().getPhone() : existingStudent.getPhone());
    
                    // Auditoría
                    existingStudent.setModifiedBy(name.getName() + " " + name.getSurname());
                    existingStudent.setUpdatedAt(LocalDateTime.now());
    
                    return studentRepository.save(existingStudent);
                })
            )
            .onErrorMap(e -> new RuntimeException("Error al actualizar el perfil del estudiante", e));
    }
    

    // Métodos para actualizar diferentes tipos de usuarios
    public Mono<User> updateUser(UserInfo userDetails, String username, String userId) {
        return userService.getFullName(username)
            .flatMap(name -> userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(existingUser -> {
                    boolean isTargetAdmin = existingUser.getRoles().contains("ROLE_ADMIN");
                    boolean isRequesterAdmin = name.getRoles().contains("ROLE_ADMIN"); // Asumiendo este método

                    if (isTargetAdmin && !isRequesterAdmin) {
                        return Mono.error(new RuntimeException("No tienes permisos para editar un usuario administrador"));
                    }
                    existingUser.setEmail(userDetails.getUser().getEmail());
                    existingUser.setPassword(passwordEncoder.encode(userDetails.getUser().getPassword()));
                    existingUser.setName(userDetails.getUser().getName());
                    existingUser.setSurname(userDetails.getUser().getSurname());
                    existingUser.setDni(userDetails.getUser().getDni());
                    existingUser.setRoles(userDetails.getUser().getRoles());
                    existingUser.setPhone(userDetails.getUser().getPhone());
                    existingUser.setModifiedBy(name.getName()+ " " + name.getSurname());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                })
            )
            .onErrorMap(e -> new RuntimeException("Error al actualizar el usuario", e));
    }
    

	// Métodos para actualizar diferentes tipos de usuarios
    public Mono<Parent> updateParent(UserInfo userDetails, String username, String parentId) {
        return userService.getFullName(username)
                .flatMap(name -> {
                    Parent parent = new Parent();
                    parent.setEmail(userDetails.getParent().getEmail());
                    parent.setPassword(passwordEncoder.encode(userDetails.getParent().getPassword()));
                    parent.setName(userDetails.getParent().getName());
                    parent.setSurname(userDetails.getParent().getSurname());
                    parent.setDni(userDetails.getParent().getDni());
                    parent.setRoles(userDetails.getParent().getRoles());
                    parent.setPhone(userDetails.getParent().getPhone());
                    parent.setCreatedBy(userDetails.getParent().getCreatedBy());
                    parent.setCreatedAt(LocalDateTime.now());
                    parent.setModifiedBy(name.getName()+ " " + name.getSurname());
                    parent.setUpdatedAt(LocalDateTime.now());
                    return parentRepository.save(parent);
                })
                .onErrorMap(e -> new RuntimeException("Error al registrar el usuario"));
    }

    public Mono<Student> updateStudent(UserInfo userDetails, String username, String studentId) {
        return userService.getFullName(username)
                .flatMap(name -> {
                    Student student = new Student();
                    student.setEmail(userDetails.getStudent().getEmail());
                    student.setPassword(passwordEncoder.encode(userDetails.getStudent().getPassword()));
                    student.setName(userDetails.getStudent().getName());
                    student.setSurname(userDetails.getStudent().getSurname());
                    student.setDni(userDetails.getStudent().getDni());
                    student.setParentId(userDetails.getStudent().getParentId());
                    student.setRoles(userDetails.getStudent().getRoles());
                    student.setPhone(userDetails.getStudent().getPhone());
                    student.setCreatedBy(userDetails.getStudent().getCreatedBy());
                    student.setCreatedAt(LocalDateTime.now());
                    student.setModifiedBy(name.getName()+ " " + name.getSurname());
                    student.setUpdatedAt(LocalDateTime.now());
                    return studentRepository.save(student);
                })
                .onErrorMap(e -> new RuntimeException("Error al registrar el estudiante"));
    }

    public Mono<Teacher> updateTeacher(UserInfo userDetails, String username, String teacherId) {
        return userService.getFullName(username)
                .flatMap(name -> {
                    Teacher teacher = new Teacher();
                    teacher.setEmail(userDetails.getTeacher().getEmail());
                    teacher.setPassword(passwordEncoder.encode(userDetails.getTeacher().getPassword()));
                    teacher.setName(userDetails.getTeacher().getName());
                    teacher.setSurname(userDetails.getTeacher().getSurname());
                    teacher.setDni(userDetails.getTeacher().getDni());
                    teacher.setInstitution(userDetails.getTeacher().getInstitution());
                    teacher.setRoles(userDetails.getTeacher().getRoles());
                    teacher.setPhone(userDetails.getTeacher().getPhone());
                    teacher.setCreatedBy(userDetails.getTeacher().getCreatedBy());
                    teacher.setCreatedAt(LocalDateTime.now());
                    teacher.setModifiedBy(name.getName()+ " " + name.getSurname());
                    teacher.setUpdatedAt(LocalDateTime.now());
                    return teacherRepository.save(teacher);
                })
                .onErrorMap(e -> new RuntimeException("Error al registrar el profesor"));
    }

    //TODO unificar el flujo de eliminacion o ver como buscar en que coleccion esta el usuario a eliminar
    // Métodos para eliminar diferentes tipos de usuarios
    public Mono<Void> deleteUser(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el usuario con ID: " + userId)))
                .flatMap(userRepository::delete);
    }

	// Métodos para eliminar diferentes tipos de usuarios
    public Mono<Void> deleteParent(String parentId) {
        return parentRepository.findById(parentId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el usuario con ID: " + parentId)))
                .flatMap(parentRepository::delete);
    }

    public Mono<Void> deleteStudent(String studentId) {
        return studentRepository.findById(studentId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el estudiante con ID: " + studentId)))
                .flatMap(studentRepository::delete);
    }

    public Mono<Void> deleteTeacher(String teacherId) {
        return teacherRepository.findById(teacherId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el profesor con ID: " + teacherId)))
                .flatMap(teacherRepository::delete);
    }

	public Mono<UserCredentialsDTO> login(String dni, String password) {
        return userRepository.findByDni(dni)
        .flatMap(user -> authenticateUser(user, user.getEmail(), password))
        .switchIfEmpty(
            studentRepository.findByDni(dni)
                .flatMap(student -> authenticateStudent(student, student.getEmail(), password))
                .switchIfEmpty(
                    teacherRepository.findByDni(dni)
                        .flatMap(teacher -> authenticateTeacher(teacher, teacher.getEmail(), password))
                        .switchIfEmpty(
                            parentRepository.findByDni(dni)
                                .flatMap(parent -> authenticateParent(parent, parent.getEmail(), password))
                                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no encontrado")))
                        )
                )
        );
    }

    public Mono<UserCredentialsDTO> authenticateStudent(Student user, String username, String password) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(),
                    user.getRoles().toArray(new String[0]));
            return Mono.just(user.getName() + " " + user.getSurname())//userService.getFullName(username)
                    .flatMap(name -> {
                        UserCredentialsDTO credentialsDTO = new UserCredentialsDTO();
                        credentialsDTO.setId(user.getId());
                        credentialsDTO.setToken(token);
                        credentialsDTO.setName(name);
                        credentialsDTO.setUsername(username);
                        credentialsDTO.setRole(user.getRoles());
                        return Mono.just(credentialsDTO);
                    });
        } else {
            return Mono.error(new RuntimeException("Credenciales incorrectas"));
        }
    }

    public Mono<UserCredentialsDTO> authenticateUser(User user, String username, String password) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(),
                    user.getRoles().toArray(new String[0]));
            return userService.getFullName(username)
                    .flatMap(name -> {
                        UserCredentialsDTO credentialsDTO = new UserCredentialsDTO();
                        credentialsDTO.setId(user.getId());
                        credentialsDTO.setToken(token);
                        credentialsDTO.setName(name.getName());
                        credentialsDTO.setUsername(username);
                        credentialsDTO.setRole(user.getRoles());
                        return Mono.just(credentialsDTO);
                    });
        } else {
            return Mono.error(new RuntimeException("Credenciales incorrectas"));
        }
    }

    public Mono<UserCredentialsDTO> authenticateParent(Parent user, String username, String password) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(),
                    user.getRoles().toArray(new String[0]));
            return Mono.just(user.getName() + " " + user.getSurname())//userService.getFullName(username)
                    .flatMap(name -> {
                        UserCredentialsDTO credentialsDTO = new UserCredentialsDTO();
                        credentialsDTO.setId(user.getId());
                        credentialsDTO.setToken(token);
                        credentialsDTO.setName(name);
                        credentialsDTO.setUsername(username);
                        credentialsDTO.setRole(user.getRoles());
                        return Mono.just(credentialsDTO);
                    });
        } else {
            return Mono.error(new RuntimeException("Credenciales incorrectas"));
        }
    }

    public Mono<UserCredentialsDTO> authenticateTeacher(Teacher user, String username, String password) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(),
                    user.getRoles().toArray(new String[0]));
            return Mono.just(user.getName() + " " + user.getSurname())//userService.getFullName(username)
                    .flatMap(name -> {
                        UserCredentialsDTO credentialsDTO = new UserCredentialsDTO();
                        credentialsDTO.setId(user.getId());
                        credentialsDTO.setToken(token);
                        credentialsDTO.setName(name);
                        credentialsDTO.setUsername(username);
                        credentialsDTO.setRole(user.getRoles());
                        return Mono.just(credentialsDTO);
                    });
        } else {
            return Mono.error(new RuntimeException("Credenciales incorrectas"));
        }
    }
    
}