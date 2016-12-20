package pro.dominion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.validator.routines.DomainValidator;
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
    		boolean domainUsage = true;
    		if(args.length > 2){
    			if(args[2].equals("-ignoreDomainUsage")){
    				domainUsage = false;
    			}
    		}
    		wikiSQL(args[1], domainUsage, em);
    	} else if(args.length > 1 && args[0].equals("-extractlines")){
    		extractlines(args[1], em);
    	} else {
    		System.out.println("not yet implemented");
    	}
        
        em.close();
        emFactory.close();
    }
    
	private static void extractlines(String fileName, EntityManager em) {
		DomainValidator domVal = DomainValidator.getInstance();
		Scanner sc;
		HashSet<String> domainSet = new HashSet<String>();
		try {
			sc= new Scanner(new BufferedReader(new FileReader(fileName)));
			sc.useDelimiter("'");
			while (sc.hasNext()) {
				String url = sc.next();
				if(url.contains("http://") || url.contains("https://")){
					String domain = url.substring(url.indexOf("://") + 3);
					if (domain.contains("/")){
						domain = domain.substring(0, domain.indexOf("/"));
					}
					if(domVal.isValid(domain)){
						domainSet.add(domain);
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(fileName.contains(".")){
			fileName = fileName.substring(0, fileName.lastIndexOf(".")).concat(".dom");
		} else {
			fileName = fileName.concat(".dom");
		}
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			Iterator<String> it = domainSet.iterator();
			while(it.hasNext()) {
			    out.write(it.next()+"\n");
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void wikiSQL(String fileName, boolean domainUsage, EntityManager em) {
		DomainValidator domVal = DomainValidator.getInstance();
		Scanner sc;
		HashSet<String> domainSet = new HashSet<String>();
		try {
			sc = new Scanner(new BufferedReader(new FileReader(fileName)));
			sc.useDelimiter("'");
			while (sc.hasNext()) {
				String url = sc.next();
				if(url.contains("http://") || url.contains("https://")){
					String domain = url.substring(url.indexOf("://") + 3);
					if (domain.contains("/")){
						domain = domain.substring(0, domain.indexOf("/"));
					}
					if(domVal.isValid(domain)){
						domainSet.add(domain);
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Found " + domainSet.size() + " entries, starting to save new domains...");
		Iterator<String> it = domainSet.iterator();
		int percentStep = ((domainSet.size()/100) > 0) ? (domainSet.size()/100) : 1;
		int counter = 0;
		int domainsSaved = 0;
		while(it.hasNext()) {
			if(addDomainString(it.next(), domainUsage, em)){
				domainsSaved++;
			}
			counter++;
			if ((counter % percentStep) == 0){
				System.out.println("Progress: " + (100 * counter) / domainSet.size() + "%");
			}
		}
		System.out.println("Saved " + domainsSaved + " new domains");
	}

	private static boolean addDomainString(String domain, boolean domainUsage, EntityManager em) {
		boolean saved = false;
		while(domain.contains(".") && !tldMap.containsKey(domain)){
			String tldString = domain.substring(domain.indexOf(".") + 1);
			if(tldMap.containsKey(tldString)){
				saved = addDomain(domain.substring(0, domain.indexOf(".")), tldString, domainUsage, em);
				break;
			} else {
				domain = domain.substring(domain.indexOf(".") + 1);
			}
		}
		return saved;
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
									addDomain(domainName, tldName, true, em);
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

	private static boolean addDomain(String domainName, String tldName, boolean domainUsage, EntityManager em) {
		boolean saved = false;
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
				if(domainUsage){
					d.setIncrementedTld(t);
				} else {
					d.setTld(t);
				}
				EntityTransaction tx = em.getTransaction();
				tx.begin();
				em.persist(d);
				em.persist(t);
				em.flush();
				tx.commit();
				saved = true;
			} else {
				System.out.println("ERROR: TLD " + tldName + " is not yet registered!");
			}
		}
		return saved;
	}

	private static void getTldMap(EntityManager em) {
        List<Tld> tlds = em.createQuery("select t from Tld t", Tld.class).getResultList();
        tldMap = new HashMap<String, Tld>();
        for(Tld t : tlds){
        	tldMap.put(t.getName(), t);
        }		
	}
}
