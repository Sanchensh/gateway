package io.sufe.gateway.filter;

public interface FilterPipeLine {

	void addLastSegment(Filter... filters);

	AbstractFilterContext get(String name);
}
