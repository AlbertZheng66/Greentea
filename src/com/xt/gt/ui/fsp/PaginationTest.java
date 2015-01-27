package com.xt.gt.ui.fsp;

import junit.framework.TestCase;

public class PaginationTest extends TestCase {

	public void testGetTotalPages() {
		Pagination p = new Pagination();
		p.setRowsPerPage(10);
		p.setTotalCount(100);
		assertEquals(10, p.getTotalPages());
		p.setTotalCount(88);
		assertEquals(9, p.getTotalPages());
		p.setTotalCount(0);
		assertEquals(0, p.getTotalPages());
		p.setTotalCount(5);
		assertEquals(1, p.getTotalPages());
		p.setRowsPerPage(17);
		p.setTotalCount(5);
		assertEquals(1, p.getTotalPages());
		p.setTotalCount(25);
		assertEquals(2, p.getTotalPages());
		p.setTotalCount(34);
		assertEquals(2, p.getTotalPages());
		
	}

}
