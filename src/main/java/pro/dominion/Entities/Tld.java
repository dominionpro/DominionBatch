package pro.dominion.Entities;

import javax.persistence.*;

@Entity
public class Tld extends DominionEntity {
	
	@Column(unique=true)
	private String name;
	private int usage;
	private String type;
	private String organisation;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void incrementUsage() {
		setUsage(this.usage + 1);
	}

	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

}
