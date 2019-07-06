package in.nimbo;

import java.util.Date;

/*simple plain old java object class containing get/set methods to store data retrieved using DAO class*/

public class News {
    private String title;
    private String dscp;
    private String link;
    private String agency;
    private Date date;

    public News(String title, String dscp, String link, String agency, Date date) {
        setTitle(title);
        setDscp(dscp);
        setLink(link);
        setAgency(agency);
        setDt(date);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDscp() {
        return dscp;
    }

    public void setDscp(String dscp) {
        this.dscp = dscp;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public Date getDate() {
        return date;
    }

    public void setDt(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return (getAgency() != null && getAgency().length() > 0 ? getAgency() + "\n" : "") + getTitle() + "\n" + (getLink() != null && getLink().length() > 0 ? getLink() + "\n" : "") +
                getDate() + "\n" + (getDscp() != null && getDscp().length() > 0 ? getDscp() : "");
    }
}
