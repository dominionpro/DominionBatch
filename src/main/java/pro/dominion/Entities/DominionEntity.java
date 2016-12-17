package pro.dominion.Entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@MappedSuperclass
public class DominionEntity {
	
    @Id
    @SequenceGenerator(name = "SEQ_DOM", sequenceName="SEQ_DOMINION", allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOM")
    protected Long id;
    @Version
    protected Long version;

}
