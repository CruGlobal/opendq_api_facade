package org.cru.mdm;

/**
 * This maps to CodId, which is the primary key for code_lookup in mdmcore
 *
 * Created by William.Randall on 6/24/14.
 */
public enum MdmCodes
{
    PERSONAL_EMAIL("1"),
    WORK_EMAIL("2"),
    HOME_PHONE("3"),
    HOME_ADDRESS("4"),
    OFFICE_ADDRESS("5");

    private String id;

    private MdmCodes(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}
