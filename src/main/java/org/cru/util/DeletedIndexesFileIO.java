package org.cru.util;

import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Util class for handling input/output for the deleted indexes file
 *
 * Created by William.Randall on 6/11/14.
 */
public class DeletedIndexesFileIO
{
    private String filename;
    private final Object lock = new Object();
    private static Logger log = Logger.getLogger(DeletedIndexesFileIO.class);

    @SuppressWarnings("unused")  //used by CDI
    public DeletedIndexesFileIO() {}

    @Inject
    public DeletedIndexesFileIO(OafProperties oafProperties)
    {
        filename = oafProperties.getProperty("deletedIndexFile");
    }

    public boolean fileContainsId(String id)
    {
        BufferedReader reader;

        synchronized (lock)
        {
            try
            {
                if(fileExists())
                {
                    reader = new BufferedReader(new FileReader(filename));

                    try
                    {
                        String line;
                        while((line = reader.readLine()) != null)
                        {
                            String lineWithoutDelimiter = line.replace(";", "");
                            if(lineWithoutDelimiter.equals(id))
                            {
                                return true;
                            }
                        }
                        return false;
                    }
                    finally
                    {
                        reader.close();
                    }
                }
                else return false;
            }
            catch(IOException ioe)
            {
                log.error("Something failed when trying to read from " + filename, ioe);
                throw new WebApplicationException(ioe);
            }
        }
    }

    public boolean fileExists()
    {
        return new File(filename).exists();
    }

    public void writeToFile(String id)
    {
        BufferedWriter writer;

        synchronized (lock)
        {
            try
            {
                File deletedIndexesFile = new File(filename);
                boolean createdFile = deletedIndexesFile.createNewFile();

                if(createdFile)
                {
                    log.info("Created file: " + filename);
                }
                if(!deletedIndexesFile.canWrite())
                {
                    if(deletedIndexesFile.setWritable(true))
                    {
                        log.info("Made " + filename + " writable.");
                    }
                }

                writer = new BufferedWriter(new FileWriter(deletedIndexesFile, true));

                try
                {
                    if(createdFile)
                    {
                        writer.append("# Deleted Indexes File #");
                    }
                    writer.append(";");  //delimiter
                    writer.newLine();
                    writer.append(id);
                    writer.flush();
                }
                finally
                {
                    writer.close();
                }
            }
            catch(IOException ioe)
            {
                log.error("Something failed when trying to write to " + filename, ioe);
                throw new WebApplicationException(ioe);
            }
        }
    }
}
