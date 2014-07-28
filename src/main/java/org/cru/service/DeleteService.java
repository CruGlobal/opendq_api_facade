package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import org.cru.model.Person;
import org.cru.model.SearchResponse;
import org.cru.qualifiers.Delete;
import org.cru.util.DeletedIndexesFileIO;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

/**
 * Service to handle complexity of deleting a {@link Person} from the index
 *
 * Created by William.Randall on 6/10/14.
 */
@Delete
public class DeleteService extends IndexingService
{
    private DeletedIndexesFileIO deletedIndexesFileIO;

    @SuppressWarnings("unused")  //used by CDI
    public DeleteService() {}

    @Inject
    public DeleteService(DeletedIndexesFileIO deletedIndexesFileIO)
    {
        this.deletedIndexesFileIO = deletedIndexesFileIO;
    }

    public void deletePerson(String id, SearchResponse foundIndex) throws ConnectException
    {
        if(!personIsDeleted(id))
        {
            deletedIndexesFileIO.writeToFile(id);
            deleteFromMdm(foundIndex);
        }
    }

    void deleteFromMdm(SearchResponse foundIndex)
    {
        DataManagementWSImpl mdmService = configureMdmService();
        String response = mdmService.deleteObject((String)foundIndex.getResultValues().getPartyId());

        if(response.contains("not found"))
        {
            throw new WebApplicationException(
                Response.status(Response.Status.NOT_FOUND)
                    .entity(response)
                    .build());
        }
    }

    public boolean personIsDeleted(String id)
    {
        return deletedIndexesFileIO.fileContainsId(id);
    }
}
