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

    public void deletePerson(String id) throws ConnectException
    {
        if(!personIsDeleted(id)) deletedIndexesFileIO.writeToFile(id);
    }

    public boolean personIsDeleted(String id)
    {
        return deletedIndexesFileIO.fileContainsId(id);
    }
}
