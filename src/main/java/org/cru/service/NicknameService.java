package org.cru.service;

import com.google.common.collect.Lists;
import com.infosolvetech.rtmatch.pdi4.RuntimeMatchWS;
import com.infosolvetech.rtmatch.pdi4.ServiceResult;
import net.java.dev.jaxb.array.AnyTypeArray;
import org.apache.log4j.Logger;
import org.cru.model.NicknameResponse;
import org.cru.model.collections.NicknameResponseList;
import org.cru.qualifiers.Nickname;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.net.ConnectException;
import java.util.List;

/**
 * Created by William.Randall on 8/18/2014.
 */
@Nickname
public class NicknameService extends IndexingService
{
    private static Logger log = Logger.getLogger(NicknameService.class);

    @Inject
    public NicknameService(OpenDQProperties openDQProperties)
    {
        this.openDQProperties = openDQProperties;
    }

    public String getStandardizedNickName(String firstName) throws ConnectException
    {
        this.slotName = "nickNameService";
        this.stepName = "RtMatchNickName";
        RuntimeMatchWS runtimeMatchWS = configureAndRetrieveRuntimeMatchService("nickname");

        ServiceResult searchResponse = runtimeMatchWS.searchSlot(slotName, Lists.newArrayList(firstName));

        if(searchResponse.isError())
        {
            log.error("Error searching index: " + searchResponse.getMessage());
            throw new WebApplicationException(searchResponse.getMessage());
        }
        NicknameResponse nicknameResponse = buildNicknameResponse(searchResponse);

        if(nicknameResponse == null)
        {
            log.warn("Could not standardize the first name");
            return firstName.toUpperCase();
        }
        return nicknameResponse.getStandardizedName();
    }

    NicknameResponse buildNicknameResponse(ServiceResult searchResult)
    {
        List<AnyTypeArray> searchResultValues = searchResult.getRows();

        if(searchResultValues == null || searchResultValues.isEmpty())
        {
            return null;
        }

        NicknameResponseList nicknameResponseList = new NicknameResponseList();

        for(int i = 0; i < searchResultValues.size(); i++)
        {
            nicknameResponseList.add(buildNicknameResponse(searchResult.getScores().get(i),
                (String)searchResultValues.get(i).getItem().get(0),
                (String)searchResultValues.get(i).getItem().get(1)));
        }

        nicknameResponseList.sortListByScore();
        return nicknameResponseList.get(0);
    }

    NicknameResponse buildNicknameResponse(Float score, String nickName, String standardizedName)
    {
        NicknameResponse nicknameResponse = new NicknameResponse();
        nicknameResponse.setScore(score);
        nicknameResponse.setNickName(nickName);
        nicknameResponse.setStandardizedName(standardizedName);

        return nicknameResponse;
    }
}
