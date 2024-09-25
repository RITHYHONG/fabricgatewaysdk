package com.example.fabricgatewaysdk.controller;

import com.example.fabricgatewaysdk.service.FabricNetworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fabric")
public class FabricController {

    private final FabricNetworkService fabricNetworkService;

    public FabricController(FabricNetworkService fabricNetworkService) {
        this.fabricNetworkService = fabricNetworkService;
    }

    @PostMapping("/invoke")
    public ResponseEntity<String> invokeTransaction(@RequestBody Map<String, String> payload) {
        try {
            fabricNetworkService.submitTransaction(payload.get("function"), payload.get("args").split(","));
            return ResponseEntity.ok("Transaction invoked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error invoking transaction: " + e.getMessage());
        }
    }

    @GetMapping("/query")
    public ResponseEntity<String> queryTransaction(@RequestParam String function, @RequestParam String args) {
        try {
            String result = fabricNetworkService.queryTransaction(function, args.split(","));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error querying transaction: " + e.getMessage());
        }
    }
}
