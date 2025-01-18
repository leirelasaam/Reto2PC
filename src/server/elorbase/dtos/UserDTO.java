package server.elorbase.dtos;

import java.util.Arrays;
import java.util.Objects;

import server.elorbase.entities.User;

/**
 * User Data Transfer Object, con los atributos necesarios solamente. El rol se
 * ha pasado a string.
 */
public class UserDTO implements java.io.Serializable {

	private static final long serialVersionUID = 8633915848476097381L;
	private Long id;
	private String role;
	private String name;
	private String email;
	private String password;
	private String lastname;
	private String pin;
	private String address;
	private String phone1;
	private String phone2;
	private byte[] photo;
	private boolean intensive;
	private boolean registered;

	public UserDTO() {
	}

	public UserDTO(User user) {
		if (user != null) {
			this.id = user.getId();
			this.role = user.getRole().getRole();
			this.name = user.getName();
			this.email = user.getEmail();
			this.password = user.getPassword();
			this.lastname = user.getLastname();
			this.pin = user.getPin();
			this.address = user.getAddress();
			this.phone1 = user.getPhone1();
			this.phone2 = user.getPhone2();
			this.photo = user.getPhoto();
			this.intensive = user.isIntensive();
			this.registered = user.isRegistered();
		}
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPin() {
		return this.pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone1() {
		return this.phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return this.phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public byte[] getPhoto() {
		return this.photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public boolean isIntensive() {
		return this.intensive;
	}

	public void setIntensive(boolean intensive) {
		this.intensive = intensive;
	}

	public boolean isRegistered() {
		return this.registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(photo);
		result = prime * result + Objects.hash(address, email, id, intensive, lastname, name, password, phone1, phone2,
				pin, registered, role);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDTO other = (UserDTO) obj;
		return Objects.equals(address, other.address) && Objects.equals(email, other.email)
				&& Objects.equals(id, other.id) && intensive == other.intensive
				&& Objects.equals(lastname, other.lastname) && Objects.equals(name, other.name)
				&& Objects.equals(password, other.password) && Objects.equals(phone1, other.phone1)
				&& Objects.equals(phone2, other.phone2) && Arrays.equals(photo, other.photo)
				&& Objects.equals(pin, other.pin) && registered == other.registered && Objects.equals(role, other.role);
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", role=" + role + ", name=" + name + ", email=" + email + ", password=" + password
				+ ", lastname=" + lastname + ", pin=" + pin + ", address=" + address + ", phone1=" + phone1
				+ ", phone2=" + phone2 + ", photo=" + Arrays.toString(photo) + ", intensive=" + intensive
				+ ", registered=" + registered + "]";
	}

}
