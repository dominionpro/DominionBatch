package pro.dominion.Entities;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints= @UniqueConstraint(columnNames={"name", "tld_id"}))
public class Domain extends DominionEntity {
	
	@ManyToOne
	private Tld tld;
	private String name;
	private String description;
	@OneToMany
	private List<Keyword> keywords;
	private Date updateTime;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Tld getTld() {
		return tld;
	}

	public void setIncrementedTld(Tld tld) {
		if(this.tld == null){
			tld.incrementUsage();
		}
		this.tld = tld;
	}
	
	public void setTld(Tld tld) {
		this.tld = tld;
	}


}
