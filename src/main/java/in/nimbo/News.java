package in.nimbo;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*simple plain old java object class containing get/set methods to store data retrieved using DAO class*/

public class News {
    private String title;
    private String dscp;
    private String agency;
    private Date dt;

    public News(String title, String dscp, String agency, Date dt) {
        setTitle(title);
        setDscp(dscp);
        setAgency(agency);
        setDt(dt);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDscp(String dscp) {
        this.dscp = dscp;
    }

    public void setAgency(String agency)
    {
        Pattern pattern = Pattern.compile("(\\w+?)@(\\w+.\\w+)");
        Matcher matcher = pattern.matcher(agency);
        if (matcher.find()){
            this.agency = matcher.group(2);
            return;
        }
        this.agency = agency;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    public String getTitle() {
        return title;
    }

    public String getDscp() {
        return dscp;
    }

    public String getAgency() {
        return agency;
    }

    public Date getDt() {
        return dt;
    }

    @Override
    public String toString() {
        return getTitle() + "\n" + (getDscp()!=null && getDscp().length()>0?getDscp()+"\n":"") +
                (getAgency()!=null && getAgency().length()>0?getAgency() + "\n":"") + getDt();
    }
}
