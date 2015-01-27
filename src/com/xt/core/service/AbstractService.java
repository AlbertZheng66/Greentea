package com.xt.core.service;

import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.proc.impl.IPOPersistenceAware;
import com.xt.core.proc.impl.fs.FileService;
import com.xt.core.proc.impl.fs.FileServiceAware;
import com.xt.gt.ui.fsp.FspParameter;
import org.apache.log4j.Logger;

abstract public class AbstractService implements IService, IPOPersistenceAware, AutoLoadable, FileServiceAware {

    private static final long serialVersionUID = -2700561025749637976L;
    
    protected transient final Logger logger = Logger.getLogger(this.getClass());
    
    /**
     * 翻页的相关参数实例。
     */
    protected FspParameter fspParameter = new FspParameter();
    /**
     * 此机会对象实例。
     */
    transient protected IPOPersistenceManager persistenceManager;
    
    /**
     * 文件服务实例
     */
    transient protected FileService fileService;

    @LocalMethod
    final public FspParameter getFspParameter() {
        if (fspParameter == null) {
            fspParameter = new FspParameter();
        }
        return fspParameter;
    }

    @LocalMethod
    final public void setFspParameter(FspParameter fspParameter) {
        if (fspParameter == null) {
            fspParameter = new FspParameter();
        }
        this.fspParameter = fspParameter;
    }

    @LocalMethod
    public void setPersistenceManager(IPOPersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    @LocalMethod
    public IPOPersistenceManager getPersistenceManager() {
        return this.persistenceManager;
    }

    @LocalMethod
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @LocalMethod
    public FileService getFileService() {
        return this.fileService;    
    }
    
    // 在此提供各种方便查询的方法。
}
