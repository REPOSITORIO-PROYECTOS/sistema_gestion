package com.sistema.gestion.DTO;

import com.sistema.gestion.Models.Profiles.Parent;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Models.Profiles.Teacher;
import com.sistema.gestion.Models.Profiles.User;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
public class UserInfo {
	private Student student;
	private Teacher teacher;
	private Parent parent;
	private User user;

	// public UserInfo(User user) {
	// 	this.user = user;
	// }

	// @JsonCreator
	// public UserInfo(@JsonProperty("student") Student student) {
	// 	this.student = student;
	// }

	// public UserInfo(Teacher teacher) {
	// 	this.teacher = teacher;
	// }

	// public UserInfo(Parent parent) {
	// 	this.parent = parent;
	// }
}
