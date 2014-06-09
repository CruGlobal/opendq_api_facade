package org.cru.service;

import org.cru.model.Address;
import org.cru.model.Person;
import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by William.Randall on 6/9/14.
 */
@Test
public class MatchingServiceTest
{
    private MatchingService matchingService;
    private OpenDQProperties openDQProperties;

    @BeforeClass
    public void setup()
    {
        openDQProperties = new OpenDQProperties();
        openDQProperties.init();

        matchingService = new MatchingService();
        matchingService.setOpenDQProperties(openDQProperties);
    }

    @Test
    public void testFindMatch() throws Exception
    {
        assertEquals(matchingService.findMatch(createTestPerson(), "test1"), "TEST_ID");
    }

    private Person createTestPerson()
    {
        Person testPerson = new Person();
        Address testAddress = new Address();

        testAddress.setAddressLine1("100 Lake Hart Dr");
        testAddress.setCity("Orlando");

        testPerson.setFirstName("Test");
        testPerson.setLastName("LastNameTest");
        testPerson.setAddress(testAddress);

        return testPerson;
    }
}
