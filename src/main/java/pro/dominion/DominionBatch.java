package pro.dominion;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    	} else {
    		System.out.println("not yet implemented");
    	}
        
        em.close();
        emFactory.close();
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

	private static void addDomain(String fullDomainName, EntityManager em) {
		String tldName = fullDomainName.substring(fullDomainName.lastIndexOf(".") + 1);
		String domainName = fullDomainName.substring(0, fullDomainName.lastIndexOf("."));
		System.out.println("tldName: " + tldName);
		System.out.println("domainName: " + domainName);
        Domain d = new Domain();
        d.setName(domainName);
        tldName = fullDomainName.substring(fullDomainName.lastIndexOf(".") + 1);
        Tld t = tldMap.get(tldName);
        if(t != null){
        	d.setTld(t);
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.persist(d);
            em.persist(t);
            em.flush();
            tx.commit();
        } else {
        	System.out.println("TLD " + tldName + " is not yet registered!");
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
