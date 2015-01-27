package com.xt.gt.ui.fsp;

import java.io.Serializable;

import com.xt.core.exception.SystemException;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * 
 * @author albert
 */
public class Pagination  implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 当有多个翻页的时候，可以给翻页指定一个名字
	 */
	private String name;

	private int startIndex = 0;

	private int totalCount = -1;
	
	/**
	 * 系统在某些情况下可能经常设置rowsPerPage参数，当设置为-1时，有时需要通过此参数将其恢复。
	 */
	public static final int DEFAULT_ROWS_PER_PAGE = SystemConfiguration.getInstance().readInt(
			SystemConstants.PAGINATION_PAGE_SIZE, 200);

	/**
	 * 每页的行数,可以通过系统参数进行配置（SystemConstants.PAGINATION_PAGE_SIZE），默认为 200 行。
	 */
	private int rowsPerPage = DEFAULT_ROWS_PER_PAGE;


	/**
	 * 最大记录，在无过滤条件的情况下，此表可能存在的最多纪录。“-1”表示此值未知。
	 */
	private int maxRowCount = -1;

    /**
     * 是否加载最大行数（Web的情况下一般是选择不加载）
     */
    private boolean loadMaxRowCount = false;

	/**
	 * 默认构造函数
	 */
	public Pagination() {
	}


	/**
	 * 将当前的计数标记翻到下一页
	 * 
	 * @return
	 */
	public boolean nextPage() {

		if (totalCount != -1 && (startIndex + rowsPerPage) >= totalCount) {
			return false;
		}
		startIndex += rowsPerPage;
		return true;
	}

	/**
	 * 将当前的计数标记设置为第一页
	 */
	public void firstPage() {
		startIndex = 0;
	}

	/**
	 * 判断当前数据是否已经加载到“最大记录”。
	 * 
	 * @return
	 */
	public boolean isMaxLoaded() {
		if (maxRowCount < 0) {
			return false;
		}
		return (startIndex + rowsPerPage > maxRowCount);
	}

	/**
	 * 判断当前数据是否已经加载到“最大记录”。
	 * 
	 * @return
	 */
	public boolean isTotalLoaded() {
		if (totalCount < 0) {
			return false;
		}
		return (startIndex + rowsPerPage > totalCount);
	}
	
    /**
     * 将rowsPerPage重新设置为初始参数。
     */
	public void resetRowsPerPage() {
		rowsPerPage = DEFAULT_ROWS_PER_PAGE;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	/**
	 * 设置每页的行数，如果行数小于0，表示加载所有数据。
	 * @param rowsPerPage
	 */
	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		if(startIndex < 0) {
			throw new SystemException("起始位置不能小于 0。");
		}
		this.startIndex = startIndex;
	}

	/**
	 * 开始页从 0 开始计算。
	 * @return 
	 */
	public int getStartPage() {
		return (rowsPerPage == 0 ? 0 : startIndex / rowsPerPage);
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPages() {
		int totalPages = totalCount / rowsPerPage;
		int mod = totalCount % rowsPerPage;
		return (mod == 0 ? totalPages : totalPages + 1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxRowCount() {
		return maxRowCount;
	}

	public void setMaxRowCount(int maxRowCount) {
		this.maxRowCount = maxRowCount;
	}

    public boolean isLoadMaxRowCount() {
        return loadMaxRowCount;
    }

    public void setLoadMaxRowCount(boolean loadMaxRowCount) {
        this.loadMaxRowCount = loadMaxRowCount;
    }

    
	
	@Override
	public String toString() {
		StringBuilder strBld = new StringBuilder();
		strBld.append(super.toString()).append("[");
		strBld.append("startIndex=").append(startIndex).append(",");
		strBld.append("maxRowCount=").append(maxRowCount).append(",");
		strBld.append("rowsPerPage=").append(rowsPerPage).append(",");
		strBld.append("totalCount=").append(totalCount).append(",");
		strBld.append("name=").append(name);
		strBld.append("]");
		return strBld.toString();
	}

}
