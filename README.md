# Keestore

>A couple of discussions from [Cryptography Stack Exchange](https://crypto.stackexchange.com/) led to exploring cryptography. In addition, I ran into [KeePass](https://www.codeproject.com/Articles/5489/KeePass-Password-Safe) and [1Password's white paper](https://1password.com/files/1Password%20for%20Teams%20White%20Paper.pdf).

## Overview
This project is a crypto tool that uses public/private keys with an additional symmetric key for encryption and decryption. **keevault** is a standalone, Java thick-client that provides the UI for the user to encrypt/decrypt key-value model data. The encrypted payload along with the user's key set are stored locally in the user's home directory.

## Crypto

### User Keys
On the first run, the user will obtain an RSA key pair and a symmetric key. The current implementation uses RSA 1024 bit keys and Triple DES with an 8-byte initializing vector. User keys are stored as a 'registration' json object in `$HOME/.keestore/keevault_registration`.

```
{
   "publicKey":(encoded),
   "privateKey":(encoded),
   "secretKey":(encrypted via publicKey),
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
Gradle builds the application and uses the `spring-boot` plugin to package the application into an executable jar file. 

```
gradle clean build
java -jar keevault/build/libs/keevault-0.1.jar
```

A checked-in jar is located at `run` for convenience.
