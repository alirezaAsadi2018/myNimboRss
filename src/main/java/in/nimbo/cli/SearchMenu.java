package in.nimbo.cli;

import asg.cliche.Command;
import in.nimbo.Filter;
import in.nimbo.SearchFilter;
import in.nimbo.dao.news_dao.NewsDao;
import in.nimbo.exception.NewsDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchMenu {
    private static final Logger logger = LoggerFactory.getLogger(SearchMenu.class);
    private SearchFilter searchFilter;
    private NewsDao newsDao;

    public SearchMenu(SearchFilter searchFilter, NewsDao newsDao) {
        this.searchFilter = searchFilter;
        this.newsDao = newsDao;
    }

    @Command (description = "clear filters")
    public void clear() {
        searchFilter.clear();
    }

    @Command (description = "add filter to content")
    public void filterTitle(String title) {
        searchFilter.addFilter(title, Filter.title);
    }

    @Command (description = "add filter to content")
    public void filterContent(String description) {
        searchFilter.addFilter(description, Filter.description);
    }

    @Command (description = "add filter to agency name")
    public void filterAgency(String agency) {
        searchFilter.addFilter(agency, Filter.agency);
    }

    @Command (description = "get news matching filters")
    public String get() {
        try {
            return newsDao.search(searchFilter).toString();
        } catch (NewsDaoException e) {
            logger.error("Error: can't search in database", e);
            return "news database error.";
        }

    }
}
