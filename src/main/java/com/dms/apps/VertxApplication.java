package com.dms.apps;

import com.dms.apps.vertx.MainLauncher;
import com.dms.apps.vertx.configs.VertxProcessor;

public class VertxApplication {
    
    public static void main(String[] args) {
        
        new MainLauncher().dispatch(new String[] {
            "run", "java:"+VertxProcessor.class.getCanonicalName()
        });
        
    }
  
}
