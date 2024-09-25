package com.example.fabricgatewaysdk.service;

import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FabricNetworkService {

    private final WalletService walletService;

    public FabricNetworkService(WalletService walletService) {
        this.walletService = walletService;
    }

    public Gateway connectGateway() throws Exception {
        // Load network configuration
        Path networkConfigPath = Paths.get("src/main/resources/connection-profile.yaml");

        // Build Gateway connection
        Gateway.Builder builder = Gateway.createBuilder()
                .identity(walletService.getWallet(), "Admin")
                .networkConfig(networkConfigPath)
                .discovery(true);

        return builder.connect();
    }

    public void submitTransaction(String transactionName, String... args) throws Exception {
        try (Gateway gateway = connectGateway()) {
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("mychaincode"); // Use correct chaincode name

            contract.submitTransaction(transactionName, args);
            System.out.println("Transaction invoked successfully");
        }
    }

    public String queryTransaction(String transactionName, String... args) throws Exception {
        try (Gateway gateway = connectGateway()) {
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("mychaincode");

            byte[] result = contract.evaluateTransaction(transactionName, args);
            return new String(result);
        }
    }
}
