package com.sistema.gestion.Auth.Services;

import java.time.LocalDateTime;

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
import com.sistema.gestion.Services.Profiles.UserService;

import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor

 public class AuthService {

	private final UserRepository userRepository;
	private final ParentsRepository parentRepository;
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
                    user.setInstitution(name.split("-")[1]);
                    user.setRoles(userDetails.getUser().getRoles());
                    user.setDni(userDetails.getUser().getDni());
                    user.setCreatedBy(name.split("-")[0]);
                    user.setCreatedAt(LocalDateTime.now());
                    user.setModifiedBy(name.split("-")[0]);
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
                    parent.setInstitution(name.split("-")[1]);
                    parent.setRoles(userDetails.getParent().getRoles());
                    parent.setPhone(userDetails.getParent().getPhone());
                    parent.setCreatedBy(name.split("-")[0]);
                    parent.setCreatedAt(LocalDateTime.now());
                    parent.setModifiedBy(name.split("-")[0]);
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
                    student.setDni(userDetails.getStudent().getDni());
                    student.setInstitution(name.split("-")[1]);
                    student.setRoles(userDetails.getStudent().getRoles());
                    student.setCreatedBy(name.split("-")[0]);
                    student.setCreatedAt(LocalDateTime.now());
                    student.setModifiedBy(name.split("-")[0]);
                    student.setUpdatedAt(LocalDateTime.now());
                    return studentRepository.save(student);
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
                    teacher.setInstitution(name.split("-")[1]);
                    teacher.setRoles(userDetails.getTeacher().getRoles());
                    teacher.setCreatedBy(name.split("-")[0]);
                    teacher.setCreatedAt(LocalDateTime.now());
                    teacher.setModifiedBy(name.split("-")[0]);
                    teacher.setUpdatedAt(LocalDateTime.now());
                    return teacherRepository.save(teacher);
                })
                .onErrorMap(e -> new RuntimeException("Error al registrar el profesor"));
    }

    // Métodos para actualizar diferentes tipos de usuarios
    public Mono<User> updateUser(UserInfo userDetails, String username, String userId) {
        return userService.getFullName(username)
            .flatMap(name -> userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(existingUser -> {
                    existingUser.setEmail(userDetails.getUser().getEmail());
                    existingUser.setPassword(passwordEncoder.encode(userDetails.getUser().getPassword()));
                    existingUser.setName(userDetails.getUser().getName());
                    existingUser.setSurname(userDetails.getUser().getSurname());
                    existingUser.setDni(userDetails.getUser().getDni());
                    existingUser.setRoles(userDetails.getUser().getRoles());
                    existingUser.setPhone(userDetails.getUser().getPhone());
                    existingUser.setModifiedBy(name);
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
                    parent.setCreatedBy(name);
                    parent.setCreatedAt(LocalDateTime.now());
                    parent.setModifiedBy(name);
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
                    student.setRoles(userDetails.getStudent().getRoles());
                    student.setPhone(userDetails.getStudent().getPhone());
                    student.setCreatedBy(name);
                    student.setCreatedAt(LocalDateTime.now());
                    student.setModifiedBy(name);
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
                    teacher.setCreatedBy(name);
                    teacher.setCreatedAt(LocalDateTime.now());
                    teacher.setModifiedBy(name);
                    teacher.setUpdatedAt(LocalDateTime.now());
                    return teacherRepository.save(teacher);
                })
                .onErrorMap(e -> new RuntimeException("Error al registrar el profesor"));
    }

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

	public Mono<UserCredentialsDTO> login(String username, String password) {
        return userRepository.findByEmail(username)
        .flatMap(user -> authenticateUser(user, username, password))
        .switchIfEmpty(
            studentRepository.findByEmail(username)
                .flatMap(student -> authenticateStudent(student, username, password))
                .switchIfEmpty(
                    teacherRepository.findByEmail(username)
                        .flatMap(teacher -> authenticateTeacher(teacher, username, password))
                        .switchIfEmpty(
                            parentRepository.findByEmail(username)
                                .flatMap(parent -> authenticateParent(parent, username, password))
                                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no encontrado")))
                        )
                )
        );
    }

    public Mono<UserCredentialsDTO> authenticateStudent(Student user, String username, String password) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(),
                    user.getRoles().toArray(new String[0]));
            return userService.getFullName(username)
                    .flatMap(name -> {
                        UserCredentialsDTO credentialsDTO = new UserCredentialsDTO();
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

    public Mono<UserCredentialsDTO> authenticateParent(Parent user, String username, String password) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(),
                    user.getRoles().toArray(new String[0]));
            return userService.getFullName(username)
                    .flatMap(name -> {
                        UserCredentialsDTO credentialsDTO = new UserCredentialsDTO();
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
            return userService.getFullName(username)
                    .flatMap(name -> {
                        UserCredentialsDTO credentialsDTO = new UserCredentialsDTO();
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