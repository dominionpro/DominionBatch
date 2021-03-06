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
	private String title;
	private String description;
	@OneToMany
	private List<Keyword> keywords;
	@OneToOne
	private Subdomain redirectSubdomain;
	private String redirectContext;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Keyword> getKeywords() {
		return keywords;
	}
	
	public void addKeyword(Keyword keyword) {
		this.keywords.add(keyword);
	}

	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
	}

	public Subdomain getRedirectSubdomain() {
		return redirectSubdomain;
	}

	public void setRedirectSubdomain(Subdomain redirectSubdomain) {
		this.redirectSubdomain = redirectSubdomain;
	}

	public String getRedirectContext() {
		return redirectContext;
	}

	public void setRedirectContext(String redirectContext) {
		this.redirectContext = redirectContext;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


}
