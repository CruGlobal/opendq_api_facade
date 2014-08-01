package org.cru.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created by William.Randall on 7/31/2014.
 */
@Test
public class ResultDataTest
{
    @Test
    public void testEquals()
    {
        assertEquals(createResultData1(), createResultData2());
        assertFalse(createResultData1().equals(createResultData3()));
    }

    private ResultData createResultData1()
    {
        ResultData resultData = new ResultData();
        resultData.putPartyId("1");
        resultData.putFirstName("Al");
        resultData.putLastName("Last");
        resultData.putAddressLine1("111 Wawa Dr");
        resultData.putAddressLine2("NULLDATA");
        resultData.putCity("Orlando");
        resultData.putState("FL");
        resultData.putZip("32832");
        resultData.putStandardizedFirstName("AL");

        return resultData;
    }

    private ResultData createResultData2()
    {
        ResultData resultData = new ResultData();
        resultData.putPartyId("2");
        resultData.putFirstName("al");
        resultData.putLastName("LaSt");
        resultData.putAddressLine1("111 WaWa Dr");
        resultData.putAddressLine2("NULLDATA");
        resultData.putCity("Orlando");
        resultData.putState("FL");
        resultData.putZip("32832");
        resultData.putStandardizedFirstName("AL");

        return resultData;
    }

    private ResultData createResultData3()
    {
        ResultData resultData = new ResultData();
        resultData.putPartyId("1");
        resultData.putFirstName("Albert");
        resultData.putLastName("Last");
        resultData.putAddressLine1("111 Wawa Dr");
        resultData.putAddressLine2("NULLDATA");
        resultData.putCity("Orlando");
        resultData.putState("FL");
        resultData.putZip("32832");
        resultData.putStandardizedFirstName("AL");

        return resultData;
    }
}
