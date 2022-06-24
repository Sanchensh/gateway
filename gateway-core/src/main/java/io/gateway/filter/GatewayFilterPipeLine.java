package io.gateway.filter;


import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//单例
public enum GatewayFilterPipeLine implements PipeLine {
    instance;
    private final Map<String, AbstractFilterContext> filterMap = new ConcurrentHashMap<>(16);

    private final LinkedList<String> filterNames = new LinkedList<>();

    @Override
    public void addFirstFilter(Filter... filters) {

    }

    private void addFirst(Filter filter) {
        if (CollectionUtils.isEmpty(filterNames)) {
            add(filter);
        } else {
            String first = filterNames.getFirst();
        }
    }

    @Override
    public void addFilter(Filter filter) {
        add(filter);
    }

    @Override
    public void addLastFilter(Filter... filters) {
        Arrays.stream(filters).forEach(this::add);
    }

    @Override
    public void addAfterFilter(String filterName, Filter... filters) {

    }

    @Override
    public void removeFilter(String filterName) {

    }

    @Override
    public AbstractFilterContext get(String filterName) {
        return filterMap.get(filterName);
    }

    private void add(Filter filter) {
        checkDuplicateName(filter.name());
        FilterContext filterContext = new FilterContext(filter);
        if (!CollectionUtils.isEmpty(filterNames)) {
            AbstractFilterContext lastFilterContext = filterMap.get(filterNames.getLast());
            lastFilterContext.next = filterContext;
        }
        filterNames.add(filter.name());
        filterMap.put(filter.name(), filterContext);
    }

    /**
     * 校验filter是否重复
     *
     * @param filterName
     */
    private void checkDuplicateName(String filterName) {
        if (filterMap.containsKey(filterName)) {
            throw new IllegalArgumentException("Duplicate filter name: " + filterName);
        }
    }
}
