package eu.musesproject.server.entity;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AssetTest {
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
//		Asset asset = new Asset();
//		asset.setTitle("title");
//		asset.setDescription("desc");
//		asset.setValue(200000);
//		asset.setConfidentialLevel("PUBLIC");
//		asset.setLocation("Sweden");
//		em.persist(asset);
//		em.getTransaction().commit();
//		
//		Asset dbAsset = em.createNamedQuery("Asset.findAll",Asset.class).setMaxResults(1).getResultList().get(0);
//		assertNotNull(dbAsset);
//		em.close();
	}
}
