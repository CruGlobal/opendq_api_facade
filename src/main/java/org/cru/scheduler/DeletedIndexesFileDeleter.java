package org.cru.scheduler;

import org.apache.log4j.Logger;
import org.cru.util.DeletedIndexesFileIO;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * The Deleted Indexes file is a temporary file to hold the IDs of people who have been
 * deleted "today".  This saves a round-trip to the MDM database when determining if a
 * person has been deleted or not.
 *
 * Each time the indexes are rebuilt, the people who have been deleted will be removed
 * from the index, so this file's data will no longer be necessary.  Thus, every time the
 * indexes are rebuilt, we should delete this temporary file.
 *
 * This class is a scheduled task to delete the Deleted Indexes file each time the indexes are rebuilt.
 *
 * Created by William.Randall on 10/23/2014.
 */
@Startup
@Singleton
public class DeletedIndexesFileDeleter
{
    private static Logger log = Logger.getLogger(DeletedIndexesFileDeleter.class);
    private String filename;

    @Inject
    private DeletedIndexesFileIO deletedIndexesFileIO;

    @Schedule(dayOfWeek  = "*", hour = "9", minute = "15", timezone = "UTC")
    public void backgroundProcessing()
    {
        filename = deletedIndexesFileIO.getFilename();
        log.info("Deleting " + filename);
        deleteFile();
    }

    private void deleteFile()
    {
        if(!deletedIndexesFileIO.fileExists())
        {
            log.warn("Unable to delete " + filename + " because file does not exist!");
            return;
        }
        if(!deletedIndexesFileIO.deleteFile())
        {
            log.warn("Failed to delete " + filename + "!");
            //TODO: Re-attempt?
        }
        log.info("Successfully deleted " + filename);
    }
}
