package com.example.textexpander.service.validation;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileNameValidationService {

    private static final Map<String, String> macroPatterns = Map.of(
            "ccd.inc", "<domain>-<trackingId>-<sourceSystem>-<version>-<frequency>-<timestamp>.<fileType>",
            "usha.india", "<domain>.<trackingId>-<sourceSystem>-<version>-<frequency>-<timestamp_milli>.<fileType>",
            "edifecs", "<domain>-<trackingId>-<sourceSystem>-<version>-<frequency>-<timestamp_milli>.<fileType>",
            "hd.i", "<unknown>.<fileType>"
    );

    private static final Map<String, Map<String, String>> macroValues = Map.of(
            "ccd.inc", Map.of("domain", "macro1", "trackingId", "value"),
            "usha.india", Map.of("domain", "macro2", "trackingId", "value2"),
            "edifecs", Map.of("domain", "macro3", "trackingId", "value3"),
            "hd.i", Map.of("unknown", "value4")
    );

    private static final Map<String, String> validationRules = Map.of(
            "domain", "^(Provider|Member|Reference|Finance|PA|TPL|Claims|Rebate|PAI|FWAFinder)$",
            "trackingId", "^[A-Z0-9]{9}$",
            "version", "^V[1-9][0-9]{0,2}$",
            "frequency", "^(Hourly|Daily|Weekly|BiWeekly|Monthly|Quarterly|BiAnnually|Annually|Event|AdHoc)$",
            "timestamp", "^\\d{14}$",
            "timestamp_milli", "^\\d{17}$",
            "fileType", "^\\.(txt|xml|csv|html|dat|zip|log|Edi|x12)$"
    );

    public String getFilePatternHavingMacros(String destinationAccountId, String agreementId, String destinationChannelId) {
        return macroPatterns.getOrDefault(destinationAccountId, null);
    }

    public Map<String, String> findValidationRuleByDestinationAccountId(String destinationAccountId) {
        // If accountId is valid, return the same map; else return empty.
        return macroPatterns.containsKey(destinationAccountId) ? validationRules : Collections.emptyMap();
    }
}