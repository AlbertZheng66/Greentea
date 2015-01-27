package com.xt.core.app;

/**
 *
 * @author Albert
 */
public interface Startable {

    public boolean init();

    public void start();
    
    public void stop();
}
