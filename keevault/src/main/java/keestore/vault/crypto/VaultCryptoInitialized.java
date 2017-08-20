/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.crypto;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * <p>
 * Once the {@code VaultCrypto} is initialized, an event will be published via
 * Spring to notify to display.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class VaultCryptoInitialized extends ApplicationContextEvent {
    private static final long serialVersionUID = 4973397719767017403L;
    
    private final VaultCrypto crypto;
    
    public VaultCryptoInitialized(ApplicationContext source, VaultCrypto crypto) {
        super(source);
        this.crypto = crypto;
    }

    public VaultCrypto getCrypto() {
        return crypto;
    }
}
