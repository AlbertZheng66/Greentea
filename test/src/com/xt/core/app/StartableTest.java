/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.core.app;


/**
 *
 * @author Albert
 */
public class StartableTest implements Startable{
    
    volatile boolean flag = false;

    public boolean init() {
        return true;
    }

    public void start() {
        while(!flag) {
            System.out.println("running........." + System.currentTimeMillis());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stop() {
        flag = true;
        System.out.println("I'm stopped........." + System.currentTimeMillis());
    }
    
}
