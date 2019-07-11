package in.nimbo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SearchFilter {
    private Map<Filter, ArrayList<String>> filters = new EnumMap<>(Filter.class);

    public SearchFilter() {
        for (Filter filter : Filter.values())
            filters.put(filter, new ArrayList<>());
    }

    public void addFilter(String text, Filter filter) {
        filters.get(filter).add(text);
    }

    /**
     * returns the texts the user has requested and wants the filtered data contain those texts
     *
     * @param filter the column of the data stored in database
     * @return the texts the filter column should contain
     */
    public List<String> getFilterTexts(Filter filter) {
        return filters.get(filter);
    }


}