package com.eric.skilltrack.controller;

import com.eric.skilltrack.service.HcTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HcController {

    private final HcTestService hcTestService;

    public HcController(HcTestService hcTestService) {
        this.hcTestService = hcTestService;
    }

    @GetMapping("/hc/test")
    public String test(@RequestParam String ldap) {
        return hcTestService.testConnection(ldap);
    }
}
