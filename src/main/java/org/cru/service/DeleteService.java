package org.cru.service;

import org.cru.model.Person;
import org.cru.util.DeletedIndexesFileIO;
import org.cru.util.OafProperties;

import javax.inject.Inject;
import java.net.ConnectException;

/**
 * Service to handle complexity of deleting a {@link Person} from the index
 *
 * Created by William.Randall on 6/10/14.
 */
public class DeleteService
{
    private OafProperties oafProperties;

    public DeleteService() {}

    @Inject
    public DeleteService(OafProperties oafProperties)
    {
        this.oafProperties = oafProperties;
    }

    public void deletePerson(String globalRegistryId) throws ConnectException
    {
        deleteFromIndex(globalRegistryId);
    }

    private void deleteFromIndex(String globalRegistryId)
    {
        addIdToDeletedIndexesFile(globalRegistryId);
    }

    void addIdToDeletedIndexesFile(String id)
    {
        DeletedIndexesFileIO deletedIndexesFileIO = new DeletedIndexesFileIO(oafProperties);
        if(!deletedIndexesFileIO.fileContainsId(id)) deletedIndexesFileIO.writeToFile(id);
    }
}
