
package com.xt.core.service.interupt;

/**
 *
 * @author Administrator
 */
public interface Cancelable {
    
    /**
     * Start a transaction and return an ID which stands for the transaction
     * @return an ID
     */
    public String start();
    
    /**
     * Set the percent, which means how much work has been done.
     * @param id an ID
     * @param ratio how much work has been done, from 0 to 100.
     */
    public void setProgress(int ratio);
    
    /**
     * get the percent of how much work has been done.
     * @param id an ID
     */
    public int getProgress();
    
    /**
     * if the transaction is cancelled by the user.
     * @param id 
     */
    public boolean isCancelled();
    
    /**
     * Cancel the transaction which is identified by the ID
     * @param id 
     */
    public void cancel();
    
   
    
}
