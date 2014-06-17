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
    private DeletedIndexesFileIO deletedIndexesFileIO;

    public DeleteService() {}

    @Inject
    public DeleteService(DeletedIndexesFileIO deletedIndexesFileIO)
    {
        this.deletedIndexesFileIO = deletedIndexesFileIO;
    }

    public void deletePerson(String globalRegistryId) throws ConnectException
    {
        deleteFromIndex(globalRegistryId);
    }

    public boolean personIsDeleted(String id)
    {
        return deletedIndexesFileIO.fileContainsId(id);
    }

    private void deleteFromIndex(String globalRegistryId)
    {
        addIdToDeletedIndexesFile(globalRegistryId);
    }

    void addIdToDeletedIndexesFile(String id)
    {
        if(!deletedIndexesFileIO.fileContainsId(id)) deletedIndexesFileIO.writeToFile(id);
    }
}
