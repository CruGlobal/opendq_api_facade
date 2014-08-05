package org.cru.mdm;

import com.infosolve.openmdm.webservices.provider.impl.ObjAttributeDataDTO;
import org.cru.data.TestPeople;
import org.cru.model.Person;
import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by William.Randall on 8/5/2014.
 */
@Test
public class PersonToMdmConverterTest
{
    private PersonToMdmConverter personToMdmConverter;

    @Test
    public void testCreateAuthProviderData()
    {
        personToMdmConverter = new PersonToMdmConverter("A");
        LocalDate today = new LocalDate();
        Person testPerson = TestPeople.generatePersonWithLotsOfData();
        List<ObjAttributeDataDTO> authProviderDataList = personToMdmConverter.createAuthProviderData(testPerson, today);

        assertNotNull(authProviderDataList);
        assertEquals(authProviderDataList.size(), 3);
        assertEquals(authProviderDataList.get(0).getField1(), "Relay");
        assertEquals(authProviderDataList.get(0).getField2(), testPerson.getAuthentication().getRelayGuidList().get(0));
        assertEquals(authProviderDataList.get(1).getField1(), "Relay");
        assertEquals(authProviderDataList.get(1).getField2(), testPerson.getAuthentication().getRelayGuidList().get(1));
        assertEquals(authProviderDataList.get(2).getField1(), "The Key");
        assertEquals(authProviderDataList.get(2).getField2(), testPerson.getAuthentication().getKeyGuidList().get(0));
    }
}
