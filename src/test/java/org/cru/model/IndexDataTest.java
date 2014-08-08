package org.cru.model;

import org.cru.model.map.IndexData;
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
    public void testEquals()
    {
        assertEquals(createIndexData1(), createIndexData2());
        assertFalse(createIndexData1().equals(createIndexData3()));
    }

    private IndexData createIndexData1()
    {
        IndexData indexData = new IndexData();
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

    private IndexData createIndexData2()
    {
        IndexData indexData = new IndexData();
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

    private IndexData createIndexData3()
    {
        IndexData indexData = new IndexData();
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
