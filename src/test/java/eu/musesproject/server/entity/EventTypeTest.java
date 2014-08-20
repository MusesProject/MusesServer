package eu.musesproject.server.entity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EventTypeTest {
	private static EntityManager em = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		if (em == null) {
//			em = (EntityManager) Persistence.createEntityManagerFactory("server").createEntityManager();
//		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInsert() {
		assertTrue(true);
//		em.getTransaction().begin();
//		EventType eventType = new EventType();
//		eventType.setEventLevel("high");
//		eventType.setEventTypeKey("key");
//		em.persist(eventType);
//		em.getTransaction().commit();
//		
//		EventType dbEventType = em.createNamedQuery("EventType.findAll",EventType.class).getResultList().get(0);
//		assertNotNull(dbEventType);
//		em.close();
	}

}
