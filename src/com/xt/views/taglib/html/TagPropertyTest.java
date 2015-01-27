package com.xt.views.taglib.html;

import junit.framework.TestCase;

public class TagPropertyTest extends TestCase {
	
	private TagProperty tp;

	public TagPropertyTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		tp = TagProperty.newInstance();
	}

	protected void tearDown() throws Exception {
		tp = null;
		super.tearDown();
	}

	public void testGetProperty() {
		assertEquals("00FF33", tp.getProperty("input_required_css"));
	}

}
