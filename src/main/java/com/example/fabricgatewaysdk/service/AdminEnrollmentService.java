package com.example.fabricgatewaysdk.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.springframework.stereotype.Service;

@Service
public class AdminEnrollmentService {

    public void enrollAdmin() throws Exception {
        // Set up HFCAClient for interacting with the CA
        Properties props = new Properties();
        props.put("pemFile", "rc/main/resources/ca-cert.pem"); // Correct certificate path
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance("https://ca.org1.example.com", props);
        caClient.setCryptoSuite(CryptoSuiteFactory.getDefault().getCryptoSuite());

        // Create wallet to store identities
        Path walletPath = Paths.get("wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);

        // Check if Admin is already enrolled
        if (wallet.get("Admin") != null) {
            System.out.println("Admin identity already exists in wallet");
            return;
        }

        // Enroll Admin
        Enrollment enrollment = caClient.enroll("admin", "adminpw");

        // Convert certificate String to X509Certificate object
        X509Certificate certificate = convertToX509Certificate(enrollment.getCert());

        // Create Admin identity using the parsed X509 certificate
        Identity adminIdentity = Identities.newX509Identity("Org1MSP", certificate, enrollment.getKey());
        wallet.put("Admin", adminIdentity);

        System.out.println("Successfully enrolled Admin and stored in wallet");
    }

    private X509Certificate convertToX509Certificate(String certString) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(certString.getBytes(StandardCharsets.UTF_8));
        return (X509Certificate) factory.generateCertificate(inputStream);
    }
}
