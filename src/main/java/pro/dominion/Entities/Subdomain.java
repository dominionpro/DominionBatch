package pro.dominion.Entities;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
//@Table(uniqueConstraints= @UniqueConstraint(columnNames={"name", "domain"}))
public class Subdomain extends DominionEntity {
	
	private String name;
	private String title;
	private String description;
	@OneToMany
	private List<Keyword> keywords;
	@OneToOne
	private Domain domain;
	private String redirectContext;
	private Date updateTime;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
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
