package org.cru.service;

import com.infosolve.openmdm.webservices.provider.impl.DataManagementWSImpl;
import com.infosolve.openmdm.webservices.provider.impl.RealTimeObjectActionDTO;
import org.cru.model.Person;
import org.cru.qualifiers.Delete;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OpenDQProperties;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

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
    public DeleteService(DeletedIndexesFileIO deletedIndexesFileIO, OpenDQProperties openDQProperties)
    {
        this.deletedIndexesFileIO = deletedIndexesFileIO;
        this.openDQProperties = openDQProperties;
    }

    public void deletePerson(String id, RealTimeObjectActionDTO foundPerson)
    {
        if(!personIsDeleted(id))
        {
            deletedIndexesFileIO.writeToFile(id);
            deleteFromMdm(foundPerson);
        }
    }

    void deleteFromMdm(RealTimeObjectActionDTO foundPerson)
    {
        DataManagementWSImpl mdmService = configureMdmService();
        try
        {
            mdmService.deleteObject(foundPerson.getObjectEntity().getPartyId());
        }
        catch(Throwable t)
        {
            if(t.getMessage().contains("not found"))
            {
                throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                        .entity(t.getMessage())
                        .build());
            }
            else throw new WebApplicationException(t.getMessage());
        }
    }

    public boolean personIsDeleted(String id)
    {
        return deletedIndexesFileIO.fileContainsId(id);
    }
}
