package pro.dominion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pro.dominion.Entities.Domain;
import pro.dominion.Entities.Tld;



/**
 * Hello world!
 *
 */
public class DominionBatch {
	
	private static Map<String, Tld> tldMap;
	
    public static void main( String[] args )
    {
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("dominion");
        EntityManager em = emFactory.createEntityManager();

        getTldMap(em);

        if(args.length > 0 && args[0].equals("-tlds")){
    		parseTLDs(em);
    	} else if(args.length > 0 && args[0].equals("-alexa")){
    		parseAlexa(em);
    	} else if(args.length > 1 && args[0].equals("-wikisql")){
    		wikiSQL(args[1], em);
    	} else {
    		System.out.println("not yet implemented");
    	}
        
        em.close();
        emFactory.close();
    }
    
	private static void wikiSQL(String fileName, EntityManager em) {
		Scanner sc;
		try {
			sc = new Scanner(new BufferedReader(new FileReader(fileName))).useDelimiter("'");
			while (sc.hasNext()) {
				String url = sc.next();
				if(url.contains("http://") || url.contains("https://")){
					String domain = url.substring(url.indexOf("://") + 3);
					if (domain.contains("/")){
						domain = domain.substring(0, domain.indexOf("/"));
					}
					// For some reason the domains are doubled and reversed in the sql file
					if(!domain.endsWith(".")){
						addDomainString(domain, em);
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
		
	}

	private static void addDomainString(String domain, EntityManager em) {
		while(domain.contains(".")){
			String tldString = domain.substring(domain.indexOf(".") + 1);
			if(tldMap.containsKey(tldString)){
				addDomain(domain.substring(0, domain.indexOf(".")), tldString, em);
				break;
			} else {
				domain = domain.substring(domain.indexOf(".") + 1);
			}
		}
	}

	private static void parseAlexa(EntityManager em) {
		Document doc;
		try {
			for (int i = 0; i < 20; i++) {
				doc = Jsoup.connect("http://www.alexa.com/topsites/global;" + Integer.toString(i)).get();
				Elements links = doc.select("a[href]");
				for (Element link : links) {
					String linkString = link.attr("abs:href").trim();
					if (linkString.contains("siteinfo")) {
						int slashIndex = linkString.lastIndexOf("/");
						if (slashIndex < linkString.length()) {
							String domainString = linkString.substring(slashIndex + 1);
							if (domainString.contains(".")) {
								String domainName = domainString.substring(0, domainString.indexOf("."));
								String tldName = domainString.substring(domainString.indexOf(".") + 1);
								if(tldName.contains(".")){
									System.out.println("ERROR: TLD " + tldName + " not processible on page " + Integer.toString(i));
								} else {
									System.out.println("Saving: " + domainName + "*" + tldName);
									addDomain(domainName, tldName, em);
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void parseTLDs(EntityManager em) {
		Document doc;
		try {
			doc = Jsoup.connect("http://www.iana.org/domains/root/db").get();

			Elements trs = doc.getElementById("tld-table").getElementsByTag("tr");
			for (Element tr : trs){
				Elements tds = tr.children();
				// Ignore headers and descriptions
				if(tds.get(0).text().startsWith(".")){
					String name = tds.get(0).text().substring(1);
					String type = tds.get(1).text();
					String organisation = tds.get(2).text();
					if(!tldMap.containsKey(name)){
						addTld(em, name, type, organisation);
					}
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addTld(EntityManager em, String name, String type, String organisation) {
		Tld t = new Tld();
		t.setName(name);
		t.setType(type);
		t.setOrganisation(organisation);
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(t);
		em.flush();
		tx.commit();
		getTldMap(em);
	}

	private static void addDomain(String domainName, String tldName, EntityManager em) {
		List res = em
				.createQuery("SELECT d.name FROM Domain d JOIN d.tld t where d.name=:domainName and t.name=:tldName)")
				.setParameter("domainName", domainName)
				.setParameter("tldName", tldName)
				.getResultList();
		if (res.size() == 0) {
			Domain d = new Domain();
			d.setName(domainName);
			Tld t = tldMap.get(tldName);
			if (t != null) {
				d.setTld(t);
				EntityTransaction tx = em.getTransaction();
				tx.begin();
				em.persist(d);
				em.persist(t);
				em.flush();
				tx.commit();
			} else {
				System.out.println("ERROR: TLD " + tldName + " is not yet registered!");
			}
		} else {
			System.out.println("Domain " + domainName + "." + tldName + " is already in DB!");
		}
	}

	private static void getTldMap(EntityManager em) {
        List<Tld> tlds = em.createQuery("select t from Tld t", Tld.class).getResultList();
        tldMap = new HashMap<String, Tld>();
        for(Tld t : tlds){
        	tldMap.put(t.getName(), t);
        }		
	}
}
