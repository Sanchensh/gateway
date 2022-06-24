package io.gateway.filter;

public interface PipeLine {
	/**
	 *在责任链头部添加filter，可同时添加多个
	 * @param filters
	 */
    void addFirstFilter(Filter... filters);
	/**
	 *在责任链尾部添加filter，只能添加一个
	 * @param filter
	 */
	void addFilter(Filter filter);
	/**
	 *在责任链最后添加filter，可同时添加多个
	 * @param filters
	 */
    void addLastFilter(Filter... filters);
	/**
	 *在责任链某个filter之后添加filter，可同时添加多个
	 * @param filters
	 */
    void addAfterFilter(String filterName, Filter... filters);

	/**
	 * 删除某个filter
	 * @param filterName 名称
	 */
	void removeFilter(String filterName);

	/**
	 * 获取某个filter
	 * @param filterName
	 * @return
	 */
    AbstractFilterContext get(String filterName);
}
