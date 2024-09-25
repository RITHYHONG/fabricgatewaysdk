package com.example.fabricgatewaysdk.service;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.nio.file.Files;

public class WalletService {
    public Wallet populateWallet() throws Exception {
        Path walletPath = Paths.get("src", "main", "resources", "wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);

        // Check if Admin identity is already in the wallet
        if (wallet.get("Admin") == null) {
            Path certPath = Paths.get("crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem");
            Path keyPath = Paths.get("crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/private_key.pem");

            X509Certificate certificate = Identities.readX509Certificate(Files.newInputStream(certPath).toString());
            PrivateKey privateKey = Identities.readPrivateKey(Files.newInputStream(keyPath).toString());

            Identity identity = Identities.newX509Identity("Org1MSP", certificate, privateKey);
            wallet.put("Admin", identity);

            System.out.println("Successfully added Admin identity to the wallet");
        }

        return wallet;
    }
}
