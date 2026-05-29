package secondBrain.service;

public class Node {
	private int id;
	private int projectId;
	private String title;
	private String content;
	private float xPosition;
	private float yPosition;
	private long createdAt;
	private int userId;

	public Node(int id, int projectId, String title, String content,
				float xPosition, float yPosition, long createdAt, int userId) {
		this.id = id;
		this.projectId = projectId;
		this.title = title;
		this.content = content;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.createdAt = createdAt;
		this.userId = userId;
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
	
	public int getUserId() { 
		return userId; 
	}

	public void setId(int id) { 
		this.id = id; 
	}
	
	public void setProjectId(int projectId) { 
		this.projectId = projectId; 
	}
	
	public void setTitle(String title) { 
		this.title = title; 
	}
	
	public void setContent(String content) { 
		this.content = content; 
	}
	
	public void setXPosition(float xPosition) { 
		this.xPosition = xPosition; 
	}
	
	public void setYPosition(float yPosition) { 
		this.yPosition = yPosition; 
	}
	
	public void setCreatedAt(long createdAt) { 
		this.createdAt = createdAt; 
	}
	public void setUserId(int userId) { 
		this.userId = userId; 
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", title=" + title + ", projectId=" + projectId + ", userId=" + userId + "]";
	}
}