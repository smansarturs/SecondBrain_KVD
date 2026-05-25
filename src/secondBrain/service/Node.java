package secondBrain.service;

public class Node {
	private int id;
	private int projectId;
	private String title;
	private String content;
	private float xPosition;
	private float yPosition;
	private long createdAt;

	public Node(int id, int projectId, String title, String content, float xPosition, float yPosition, long createdAt) {
		this.id = id;
		this.projectId = projectId;
		this.title = title;
		this.content = content;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
	}

	public int getProjectId() {
		return projectId;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public float getXPosition() {
		return xPosition;
	}

	public float getYPosition() {
		return yPosition;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", projectId=" + projectId + ", title=" + title + ", xPosition=" + xPosition
				+ ", yPosition=" + yPosition + ", createdAt=" + createdAt + "]";
	}
}
