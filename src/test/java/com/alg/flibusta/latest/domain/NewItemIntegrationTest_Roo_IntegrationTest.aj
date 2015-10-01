// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.alg.flibusta.latest.domain;

import com.alg.flibusta.latest.domain.NewItem;
import com.alg.flibusta.latest.domain.NewItemDataOnDemand;
import com.alg.flibusta.latest.domain.NewItemIntegrationTest;
import java.util.Iterator;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect NewItemIntegrationTest_Roo_IntegrationTest {
    
    declare @type: NewItemIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: NewItemIntegrationTest: @ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext*.xml");
    
    declare @type: NewItemIntegrationTest: @Transactional;
    
    @Autowired
    NewItemDataOnDemand NewItemIntegrationTest.dod;
    
    @Test
    public void NewItemIntegrationTest.testCountNewItems() {
        Assert.assertNotNull("Data on demand for 'NewItem' failed to initialize correctly", dod.getRandomNewItem());
        long count = NewItem.countNewItems();
        Assert.assertTrue("Counter for 'NewItem' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void NewItemIntegrationTest.testFindNewItem() {
        NewItem obj = dod.getRandomNewItem();
        Assert.assertNotNull("Data on demand for 'NewItem' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'NewItem' failed to provide an identifier", id);
        obj = NewItem.findNewItem(id);
        Assert.assertNotNull("Find method for 'NewItem' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'NewItem' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void NewItemIntegrationTest.testFindAllNewItems() {
        Assert.assertNotNull("Data on demand for 'NewItem' failed to initialize correctly", dod.getRandomNewItem());
        long count = NewItem.countNewItems();
        Assert.assertTrue("Too expensive to perform a find all test for 'NewItem', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<NewItem> result = NewItem.findAllNewItems();
        Assert.assertNotNull("Find all method for 'NewItem' illegally returned null", result);
        Assert.assertTrue("Find all method for 'NewItem' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void NewItemIntegrationTest.testFindNewItemEntries() {
        Assert.assertNotNull("Data on demand for 'NewItem' failed to initialize correctly", dod.getRandomNewItem());
        long count = NewItem.countNewItems();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<NewItem> result = NewItem.findNewItemEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'NewItem' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'NewItem' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void NewItemIntegrationTest.testFlush() {
        NewItem obj = dod.getRandomNewItem();
        Assert.assertNotNull("Data on demand for 'NewItem' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'NewItem' failed to provide an identifier", id);
        obj = NewItem.findNewItem(id);
        Assert.assertNotNull("Find method for 'NewItem' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyNewItem(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'NewItem' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void NewItemIntegrationTest.testMergeUpdate() {
        NewItem obj = dod.getRandomNewItem();
        Assert.assertNotNull("Data on demand for 'NewItem' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'NewItem' failed to provide an identifier", id);
        obj = NewItem.findNewItem(id);
        boolean modified =  dod.modifyNewItem(obj);
        Integer currentVersion = obj.getVersion();
        NewItem merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'NewItem' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void NewItemIntegrationTest.testPersist() {
        Assert.assertNotNull("Data on demand for 'NewItem' failed to initialize correctly", dod.getRandomNewItem());
        NewItem obj = dod.getNewTransientNewItem(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'NewItem' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'NewItem' identifier to be null", obj.getId());
        try {
            obj.persist();
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        obj.flush();
        Assert.assertNotNull("Expected 'NewItem' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void NewItemIntegrationTest.testRemove() {
        NewItem obj = dod.getRandomNewItem();
        Assert.assertNotNull("Data on demand for 'NewItem' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'NewItem' failed to provide an identifier", id);
        obj = NewItem.findNewItem(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'NewItem' with identifier '" + id + "'", NewItem.findNewItem(id));
    }
    
}
