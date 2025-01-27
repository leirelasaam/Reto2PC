package server.elorbase.entities;

import java.io.Serializable;
import java.util.Objects;

public class TeacherSchedule implements Serializable {
	private static final long serialVersionUID = 8370298203148997612L;
	private String event;
	private byte day;
	private byte hour;
	private String type;
	private String status;
	private Long meetingId;

	public TeacherSchedule() {

	}

	public TeacherSchedule(String event, byte day, byte hour, String type, String status, Long meetingId) {
		super();
		this.event = event;
		this.day = day;
		this.hour = hour;
		this.type = type;
		this.status = status;
		this.meetingId = meetingId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(day, event, hour, meetingId, status, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeacherSchedule other = (TeacherSchedule) obj;
		return day == other.day && Objects.equals(event, other.event) && hour == other.hour
				&& Objects.equals(meetingId, other.meetingId) && Objects.equals(status, other.status)
				&& Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return "TeacherSchedule [event=" + event + ", day=" + day + ", hour=" + hour + ", type=" + type + ", status="
				+ status + ", meetingId=" + meetingId + "]";
	}

}
