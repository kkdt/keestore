/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault;

import org.apache.log4j.Logger;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

/**
 * <p>
 * The main application container that uses Spring Boot for initialization.
 * </p>
 * 
 * @author thinh ho
 *
 */
@SpringBootApplication
@ImportResource({
   "classpath:keestore/vault/spring/keeVault.xml"
})
public class KeeVault {
   private static final Logger logger = Logger.getLogger(KeeVault.class);
   
   public static void main(String[] args) {
      logger.debug("======================================================");
      logger.debug("Starting application " + KeeVault.class.getSimpleName());
      logger.debug("======================================================");
      
      SpringApplicationBuilder app = new SpringApplicationBuilder(KeeVault.class)
         .bannerMode(Mode.OFF).logStartupInfo(false);
      app.headless(false).web(false)
         .run(args);
   }

}
