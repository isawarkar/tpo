package tpo.beans;

public class Resource {
	private String id;
	private String name;
	private String color;

	public Resource(String name, String color) {
		this.name = name;
		this.id = name;
		this.color = color;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
