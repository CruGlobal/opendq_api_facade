package org.cru.model;

import org.cru.model.map.NameAndAddressIndexData;
import org.cru.model.map.NameAndCommunicationIndexData;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created by William.Randall on 7/31/2014.
 */
@Test
public class IndexDataTest
{
    @Test
    public void testNameAndAddressDataEquals()
    {
        assertEquals(createAddressIndexData1(), createAddressIndexData2());
        assertFalse(createAddressIndexData1().equals(createAddressIndexData3()));
    }

    @Test
    public void testNameAndEmailDataEquals()
    {
        assertEquals(createEmailIndexData1(), createEmailIndexData2());
        assertFalse(createEmailIndexData1().equals(createEmailIndexData3()));
    }

    private NameAndAddressIndexData createAddressIndexData1()
    {
        NameAndAddressIndexData indexData = new NameAndAddressIndexData();
        indexData.putPartyId("1");
        indexData.putFirstName("Al");
        indexData.putLastName("Last");
        indexData.putAddressLine1("111 Wawa Dr");
        indexData.putAddressLine2("NULLDATA");
        indexData.putCity("Orlando");
        indexData.putState("FL");
        indexData.putZipCode("32832");
        indexData.putStandardizedFirstName("AL");

        return indexData;
    }

    private NameAndAddressIndexData createAddressIndexData2()
    {
        NameAndAddressIndexData indexData = new NameAndAddressIndexData();
        indexData.putPartyId("2");
        indexData.putFirstName("al");
        indexData.putLastName("LaSt");
        indexData.putAddressLine1("111 WaWa Dr");
        indexData.putAddressLine2("NULLDATA");
        indexData.putCity("Orlando");
        indexData.putState("FL");
        indexData.putZipCode("32832");
        indexData.putStandardizedFirstName("AL");

        return indexData;
    }

    private NameAndAddressIndexData createAddressIndexData3()
    {
        NameAndAddressIndexData indexData = new NameAndAddressIndexData();
        indexData.putPartyId("1");
        indexData.putFirstName("Albert");
        indexData.putLastName("Last");
        indexData.putAddressLine1("111 Wawa Dr");
        indexData.putAddressLine2("NULLDATA");
        indexData.putCity("Orlando");
        indexData.putState("FL");
        indexData.putZipCode("32832");
        indexData.putStandardizedFirstName("AL");

        return indexData;
    }

    private NameAndCommunicationIndexData createEmailIndexData1()
    {
        NameAndCommunicationIndexData indexData = new NameAndCommunicationIndexData();
        indexData.putPartyId("1");
        indexData.putFirstName("Al");
        indexData.putLastName("Last");
        indexData.putCommunicationData("al.last@cru.org");
        indexData.putGlobalRegistryId("111-222-333");
        indexData.putStandardizedFirstName("AL");

        return indexData;
    }

    private NameAndCommunicationIndexData createEmailIndexData2()
    {
        NameAndCommunicationIndexData indexData = new NameAndCommunicationIndexData();
        indexData.putPartyId("2");
        indexData.putFirstName("AL");
        indexData.putLastName("lAsT");
        indexData.putCommunicationData("al.last@cru.org");
        indexData.putGlobalRegistryId("111-222-333");
        indexData.putStandardizedFirstName("AL");

        return indexData;
    }

    private NameAndCommunicationIndexData createEmailIndexData3()
    {
        NameAndCommunicationIndexData indexData = new NameAndCommunicationIndexData();
        indexData.putPartyId("1");
        indexData.putFirstName("Al");
        indexData.putLastName("Last");
        indexData.putCommunicationData("allast@yahoo.com");
        indexData.putGlobalRegistryId("44422-66234");
        indexData.putStandardizedFirstName("AL");

        return indexData;
    }
}
