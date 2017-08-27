# Keestore

>A couple of discussions from [Cryptography Stack Exchange](https://crypto.stackexchange.com/) led to exploring cryptography. In addition, I ran into [KeePass](https://www.codeproject.com/Articles/5489/KeePass-Password-Safe) and [1Password's white paper](https://1password.com/files/1Password%20for%20Teams%20White%20Paper.pdf) and started coding for practice. The goal is to implement purely with Java crypto initially, then move to other libraries when I get a better understanding basic crypto.

## Overview
This project is a crypto tool that uses public/private keys with an additional symmetric key for encryption and decryption. **keevault** is a standalone, Java thick-client that provides the UI for the user to encrypt/decrypt key-value model data. The encrypted payload along with the user's key set are stored locally in the user's home directory. All encryption and decryption occurs locally on the user's machine; therefore, the crypto for keevault does not address related risks with "across the wire" data transmission.

## Keevault
Keevault is a standalone, thick-client application that runs on the user's local machine. Application data is saved in `$HOME/.keestore` and includes:

1. `logs` - Application logs
2. `keevault_registration` - User registration details that includes the crypto keys
3. `<uniqueid>` file - The encrypted storage for data inputs into the UI (i.e. `$HOME/.keestore/ce8d6e3b-7791-497d-9310-0dd94ee50ed6`)

The user creates a "vault" which is just a basic bin for one or more key-value items that are saved encrypted. The user can create many vaults and each vault will have a unique identifier that is hidden from the user; therefore, a user can have a vault with the same name. However, vault items within a single vault must have a unique key.

## Crypto

### User Keys
On the first run, the user will obtain an RSA key pair and a symmetric key. The current implementation uses RSA 2048 bit keys and AES128 with an 16-byte initializing vector. User keys are stored as a 'registration' json object in `$HOME/.keestore/keevault_registration`.

```

   "publicKey":(encoded),
   "privateKey":(encoded),
   "secretKey":(encrypted via publicKey),
   "salt":(random 8-byte salt)
   "itemId":(unique registration identifier)
   "itemName":"registration",
}
```

### Encryption
Encryption occurs on the user's key-value data models. The IV will be packaged/prepended to the encrypted data and _this_ payload is the encrypted payload.

1. Sign the original, unencrypted payload
2. Encrypt the payload via the `secretKey`
3. Encrypt `secretKey` via the `publicKey`
4. Create an encrypted datastore in json format

```
{
   "signature":(encoded),
   "payload":(encrypted),
   "secretKey":(encrypted symmetric key),
   "itemName":"encrypted",
   "itemId":(unique identifier)
}
```

### Decryption
Decryption occurs on the local encrypted datastore and loaded into the UI. The IV is extracted from its expected location and used to obtain the decrypted payload.

1. Decrypt `secretKey` using the user's `privateKey`
2. Decrypt `payload` using the decrypted `secretKey`
3. Compare `signature`

## Building
Gradle builds the application and uses the following plugins to build distributable artifacts.

1. [Gradle](https://docs.gradle.org/current/userguide/distribution_plugin.html) `distribution`
2. [Spring](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-gradle-plugin.html) `spring-boot`
3. [crotwell](https://github.com/crotwell) [MacApp Bundle](https://github.com/crotwell/gradle-macappbundle)

```
gradle clean build
```

Navigate to `keevault/build/distributions` where there will be the following files for running **keevault** in Mac/Unix/Windows environments:

1. `dmg` file to install and run in Mac OSX
2. `tar` file packaged with the Unix and Windows runscripts along with all necessary dependencies
3. `zip` file which contains the same contents as the `tar` file
