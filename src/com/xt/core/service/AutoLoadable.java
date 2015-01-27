package com.xt.core.service;

import java.io.Serializable;

import com.xt.gt.ui.fsp.FspParameter;

public interface AutoLoadable extends Serializable {
	
	@LocalMethod
	public FspParameter getFspParameter();

	@LocalMethod
	public void setFspParameter(FspParameter fspParameter);
}
