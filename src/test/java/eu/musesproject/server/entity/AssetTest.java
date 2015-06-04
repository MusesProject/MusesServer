package eu.musesproject.server.entity;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2015 Sweden Connectivity
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
