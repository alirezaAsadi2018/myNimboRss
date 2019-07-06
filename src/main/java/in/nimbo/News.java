package in.nimbo;

import java.util.Date;

/*simple plain old java object class containing get/set methods to store data retrieved using DAO class*/

public class News {
    private String title;
    private String dscp;
    private String link;
    private Date dt;

    public News(String title, String dscp, String link, Date dt) {
        setTitle(title);
        setDscp(dscp);
        setLink(link);
        setDt(dt);
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

    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    @Override
    public String toString() {
        return getTitle() + "\n" + (getLink() != null && getLink().length() > 0 ? getLink() + "\n" : "") +
                getDt() + "\n" + (getDscp() != null && getDscp().length() > 0 ? getDscp() : "");
    }
}
