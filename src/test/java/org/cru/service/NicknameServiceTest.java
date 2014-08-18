package org.cru.service;

import org.cru.util.OpenDQProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.ConnectException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by William.Randall on 8/18/2014.
 */
@Test
public class NicknameServiceTest
{
    private NicknameService nicknameService;

    @BeforeClass
    public void setup()
    {
        OpenDQProperties openDQProperties = new OpenDQProperties();
        openDQProperties.init();
        nicknameService = new NicknameService(openDQProperties);
    }

    @DataProvider
    private Object[][] getNicknames()
    {
        return new Object[][] {
            { "Bobby", "ROBERT" },
            { "Bob", "ROBERT" },
            { "Davey", "DAVID" },
            { "Dave", "DAVID" },
            { "Bill", "WILLIAM" }
        };
    }

    @Test(dataProvider = "getNicknames")
    public void testGetStandardizedNickName(String nickname, String standardizedName) throws ConnectException
    {
        String foundName = nicknameService.getStandardizedNickName(nickname);
        assertNotNull(foundName);
        assertEquals(foundName, standardizedName);
    }
}
