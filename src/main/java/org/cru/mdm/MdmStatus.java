package org.cru.mdm;

/**
 * Created by William.Randall on 6/30/14.
 */
public enum MdmStatus
{
    APPROVED("A"),
    PENDING("P"),
    REJECTED("R");

    private String statusCode;

    private MdmStatus(String statusCode)
    {
        this.statusCode = statusCode;
    }

    public String getStatusCode()
    {
        return statusCode;
    }
}
