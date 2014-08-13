package org.cru.model;

/**
 * Holds information returned from OpenDQ about nickname matches
 * in order to access desired properties more easily.
 *
 * Created by William.Randall on 8/13/2014.
 */
public class NicknameResponse
{
    private double score;
    private String nickName;
    private String standardizedName;

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getStandardizedName()
    {
        return standardizedName;
    }

    public void setStandardizedName(String standardizedName)
    {
        this.standardizedName = standardizedName;
    }
}
