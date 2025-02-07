package server.elorbase.entities;

import java.io.Serializable;
import java.util.Objects;

public class StudentSchedule implements Serializable {
	private static final long serialVersionUID = 8370298203148997612L;
	private String module;
	private byte day;
	private byte hour;


	public StudentSchedule() {

	}

	public StudentSchedule(String module, byte day, byte hour) {
		super();
		this.module = module;
		this.day = day;
		this.hour = hour;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public byte getDay() {
		return day;
	}

	public void setDay(byte day) {
		this.day = day;
	}

	public byte getHour() {
		return hour;
	}

	public void setHour(byte hour) {
		this.hour = hour;
	}

	

	@Override
	public int hashCode() {
		return Objects.hash(day, module, hour);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentSchedule other = (StudentSchedule) obj;
		return day == other.day && Objects.equals(module, other.module) && hour == other.hour;
	}

	@Override
	public String toString() {
		return "StudentSchedule [hour=" + hour + ", day=" + day + ", module="  + module +" ]";
	}

}
