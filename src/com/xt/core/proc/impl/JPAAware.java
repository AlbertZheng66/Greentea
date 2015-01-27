package com.xt.core.proc.impl;

import javax.persistence.EntityManager;

public interface JPAAware {
	
	public void setEntityManager(EntityManager em);

    public void getEntityManager(EntityManager em);

}
