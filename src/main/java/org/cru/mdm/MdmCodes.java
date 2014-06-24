package org.cru.mdm;

/**
 * This maps to CodId, which is the primary key for code_lookup in mdmcore
 *
 * Created by William.Randall on 6/24/14.
 */
public enum MdmCodes
{
    MAILING_ADDRESS("15"),
    BILLING_ADDRESS("16"),
    PRIMARY_EMAIL("23"),
    SECONDARY_EMAIL("25"),
    PRIMARY_PHONE("29"),
    SECONDARY_PHONE("30");

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
