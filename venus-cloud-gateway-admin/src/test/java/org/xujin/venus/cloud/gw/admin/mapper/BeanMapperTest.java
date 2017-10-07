package org.xujin.venus.cloud.gw.admin.mapper;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.xujin.venus.cloud.gw.admin.utils.BeanMapper;

public class BeanMapperTest {

	@Test
	public void copySingleObject() {
		Student student = new Student("zhang3", 20, new Teacher("li4"), null);
		StudentVO studentVo = BeanMapper.map(student, StudentVO.class);
		System.out.println(String.valueOf(studentVo));

	}

	public static class Student {
		public String name;
		private int age;
		private Teacher teacher;
		private List<String> course = new ArrayList();

		public Student(String name, int age, Teacher teacher, List<String> course) {
			this.name = name;
			this.age = age;
			this.teacher = teacher;
			this.course = course;
		}

		public List<String> getCourse() {
			return course;
		}

		public void setCourse(List<String> course) {
			this.course = course;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public Teacher getTeacher() {
			return teacher;
		}

		public void setTeacher(Teacher teacher) {
			this.teacher = teacher;
		}

	}

	public static class Teacher {
		private String name;

		public Teacher(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class StudentVO {
		public String name;
		private int age;
		private TeacherVO teacher;
		private List<String> course = new ArrayList();

		public StudentVO(String name, int age, TeacherVO teacher, List<String> course) {
			this.name = name;
			this.age = age;
			this.teacher = teacher;
			this.course = course;
		}

		public List<String> getCourse() {
			return course;
		}

		public void setCourse(List<String> course) {
			this.course = course;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public TeacherVO getTeacher() {
			return teacher;
		}

		public void setTeacher(TeacherVO teacher) {
			this.teacher = teacher;
		}

	}

	public static class TeacherVO {
		private String name;

		public TeacherVO(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}