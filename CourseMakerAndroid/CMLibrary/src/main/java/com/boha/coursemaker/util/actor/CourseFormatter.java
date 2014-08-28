package com.boha.coursemaker.util.actor;

import com.boha.coursemaker.dto.CourseDTO;

public class CourseFormatter {

	public CourseFormatter() {
		// TODO Auto-generated constructor stub
	}

	public static String format(CourseDTO course) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html>").append(LINE_BREAK);
		sb.append("<head>").append(LINE_BREAK);
		sb.append("<title>").append(course.getCourseName()).append("</title>").append(LINE_BREAK);
		sb.append("</head>").append(LINE_BREAK);
		sb.append("<body>").append(LINE_BREAK);
		
		sb.append("<h1>").append(course.getCourseName()).append("</h1>").append(LINE_BREAK);
		sb.append("<p>").append(course.getDescription()).append("</p>").append(LINE_BREAK);
		
		sb.append("<h2>").append("Tasks/Lessons").append("</h2>").append(LINE_BREAK);
//		for (LessonDTO lesson : course.getLessonList()) {
//			sb.append("<h3>").append(lesson.getLessonName()).append("</h3>").append(LINE_BREAK);
//			sb.append("<p>").append(lesson.getDescription()).append("</p>").append(LINE_BREAK);
//
//		}
		
		
		
		
		sb.append("</body>").append(LINE_BREAK);
		sb.append("</html>").append(LINE_BREAK);
		
		return sb.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	static final String LINE_BREAK = "\n";
}
