package com.xt.gt.jt.proc.result.ajax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List aa = new ArrayList();
		Object bb = aa;
		Collection ll = (Collection)bb;
		System.out.println(ll.toString());
	}

}
