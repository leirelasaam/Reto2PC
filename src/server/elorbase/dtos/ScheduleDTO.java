package server.elorbase.dtos;

import server.elorbase.entities.Schedule;

public class ScheduleDTO {
	private Long id;
	private String module;
	private String teacher;
	private byte day;
	private byte hour;

	public ScheduleDTO() {
	}
	
	public ScheduleDTO(Schedule schedule) {
		if (schedule != null) {
			this.id = schedule.getId();
			this.module = schedule.getModule().getName();
			this.teacher = schedule.getModule().getUser().getName() + " " + schedule.getModule().getUser().getLastname();
			this.day = schedule.getDay();
			this.hour = schedule.getHour();
		}
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModule() {
		return this.module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public byte getDay() {
		return this.day;
	}

	public void setDay(byte day) {
		this.day = day;
	}

	public byte getHour() {
		return this.hour;
	}

	public void setHour(byte hour) {
		this.hour = hour;
	}

}
