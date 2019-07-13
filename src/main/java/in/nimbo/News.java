package in.nimbo;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/*simple plain old java object class containing get/set methods to store data retrieved using DAO class*/
public class News {

    private static final DateTimeFormatter readFormatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss 'IRDT' yyyy");
    private static final DateTimeFormatter writeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String title;
    //description is the main content(description) of the news object
    private String description;
    private String link;
    private String agency;
    private Timestamp date;

    public News(String title, String description, String link, String agency, String date) {
        setTitle(title);
        setDescription(description);
        setLink(link);
        setAgency(agency);
        setDate(date);
    }

    public News(String title, String description, String link, String agency, Timestamp date) {
        setTitle(title);
        setDescription(description);
        setLink(link);
        setAgency(agency);
        setDate(date);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(String date) {

        this.date = Timestamp.valueOf(writeFormatter.format(readFormatter.parse(date)));
    }

    private void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof News))
            return false;
        News that = (News) o;
        return this.agency.equals(that.agency) &&
                this.description.equals(that.description) &&
                this.link.equals(that.link) &&
                this.title.equals(that.title) &&
                this.date.equals(that.date);
    }


    @Override
    public int hashCode() {
        int result = 0;
        result = result * 37 + (title != null ? (title.hashCode()) : 0);
        result = result * 23 + (link != null ? (link.hashCode()) : 0);
        result = result * 31 + (date != null ? (date.hashCode()) : 0);
        return result;
    }

    @Override
    public String toString() {
        return (getAgency() != null && getAgency().length() > 0 ? getAgency() + "\n" : "") + getTitle() + "\n" + (getLink() != null && getLink().length() > 0 ? getLink() + "\n" : "") +
                getDate() + "\n" + (getDescription() != null && getDescription().length() > 0 ? getDescription() : "");
    }
}
