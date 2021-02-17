package com.meidianyi.shop.common.foundation.util;
/**
 * 
 * @author 新国
 *
 */
public class Page {
	
	/**
	 *  添加分页列表默认当前页码和默认显示行数
	 *  @author 李晓冰
	 */
	public static final int DEFAULT_CURRENT_PAGE=1;
	public static final int DEFAULT_PAGE_ROWS=20;
	
	public Integer totalRows;
	public Integer currentPage;
	public Integer firstPage;
	public Integer prePage;
	public Integer nextPage;
	public Integer lastPage;
	public Integer pageRows;
	public Integer pageCount;
	
	/**
	 * @author 黄壮壮
	 */
	public Page() {}

	public static Page getPage(Integer totalRows, Integer currentPage, Integer pageRows) {
		//currentPage pageRows为null取默认值;<1取默认
		currentPage = currentPage == null || currentPage.intValue() < 1 ? DEFAULT_CURRENT_PAGE : currentPage;
		pageRows = pageRows == null || pageRows.intValue() < 1 ? DEFAULT_PAGE_ROWS : pageRows;
		Integer pageCount = (Integer)(int)Math.ceil(Double.valueOf(totalRows) / Double.valueOf(pageRows));
		Integer lastPage = pageCount.intValue() > 0 ? pageCount : 1;
		currentPage = currentPage.intValue() > lastPage.intValue() ? lastPage : (currentPage.intValue() <= 0 ? 1 : currentPage);
		Integer firstPage = 1;
		Integer prePage = currentPage.intValue() > 1 ? currentPage - 1 : 1;
		Integer nextPage = currentPage.intValue() >= lastPage.intValue() ? lastPage : currentPage + 1;
		return new Page(totalRows, currentPage, firstPage, prePage, nextPage, lastPage, pageRows,pageCount);
	}

	public Page(Integer totalRows, Integer currentPage, Integer firstPage, Integer prePage, Integer nextPage,
			Integer lastPage, Integer pageRows, Integer pageCount) {
		super();
		this.totalRows = totalRows;
		this.currentPage = currentPage;
		this.firstPage = firstPage;
		this.prePage = prePage;
		this.nextPage = nextPage;
		this.lastPage = lastPage;
		this.pageRows = pageRows;
		this.pageCount = pageCount;
	}

	public Integer getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(Integer firstPage) {
		this.firstPage = firstPage;
	}

	public Integer getPrePage() {
		return prePage;
	}

	public void setPrePage(Integer prePage) {
		this.prePage = prePage;
	}

	public Integer getNextPage() {
		return nextPage;
	}

	public void setNextPage(Integer nextPage) {
		this.nextPage = nextPage;
	}

	public Integer getLastPage() {
		return lastPage;
	}

	public void setLastPage(Integer lastPage) {
		this.lastPage = lastPage;
	}

	public Integer getPageRows() {
		return pageRows;
	}

	public void setPageRows(Integer pageRows) {
		this.pageRows = pageRows;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	@Override
	public String toString() {
		return "Page [totalRows=" + totalRows + ", currentPage=" + currentPage + ", firstPage=" + firstPage
				+ ", prePage=" + prePage + ", nextPage=" + nextPage + ", lastPage=" + lastPage + ", pageRows="
				+ pageRows + ", pageCount=" + pageCount + "]";
	}

    
}
