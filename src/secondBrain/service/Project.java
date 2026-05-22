package secondBrain.service;

public class Project {
	private int id;
	private int userId;
	private String name;
	private long createdAt;

	public Project(int id, int userId, String name, long createdAt) {
		this.id = id;
		this.userId = userId;
		this.name = name;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", userId=" + userId + ", name=" + name + ", createdAt=" + createdAt + "]";
	}
}
