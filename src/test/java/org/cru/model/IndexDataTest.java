package org.cru.model;

import org.cru.model.map.IndexData;
import org.cru.model.map.NameAndAddressIndexData;
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
        assertEquals(createIndexData1(), createIndexData2());
        assertFalse(createIndexData1().equals(createIndexData3()));
    }

    private NameAndAddressIndexData createIndexData1()
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

    private NameAndAddressIndexData createIndexData2()
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

    private NameAndAddressIndexData createIndexData3()
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
}
