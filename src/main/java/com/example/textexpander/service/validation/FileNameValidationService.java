package com.example.textexpander.service.validation;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileNameValidationService {

    private static final Map<String, String> macroPatterns = Map.of(
            "ccd.inc", "<domain>-<filedetailstrackingId>-<sourceSystem>-<version>-<frequency>-<timestamp>.<fileType>",
            "usha.india", "<domain>.<filedetailstrackingId>-<sourceSystem>-<version>-<frequency>-<timestamp_milli>.<fileType>",
            "edifecs", "<domain>-<filedetailstrackingId>-<sourceSystem>-<version>-<frequency>-<timestamp_milli>.<fileType>",
            "hd.i", "<unknown>.<fileType>"
    );

    private static final Map<String, Map<String, String>> macroValues = Map.of(
            "ccd.inc", Map.of("domain", "macro1", "trackingId", "value"),
            "usha.india", Map.of("domain", "macro2", "trackingId", "value2"),
            "edifecs", Map.of("domain", "macro3", "trackingId", "value3"),
            "hd.i", Map.of("unknown", "value4")
    );

    private static final Map<String, String> validationRules = Map.of(
            "domain", "^(?i)(Provider|Member|Reference|Finance|PA|TPL|Claims|Rebate|PAI|FWAFinder)$",
            "filedetailstrackingId", "^(?i)[A-Z0-9_]+_[A-Z0-9_]{9}",
            "sourceSystem", "^(?i)(PBMS|EDI|MMIS|FWAFinder|EDW)$",
            "version", "^(?i)V[1-9][0-9]{0,2}$",
            "frequency", "^(?i)(Hourly|Daily|Weekly|BiWeekly|Monthly|Quarterly|BiAnnually|Annually|Event|AdHoc)$",
            "timestamp", "^\\d{14}$",
            //"timestamp_milli", "^\\d{17}$",
            "fileType", "^(?i)\\.(txt|xml|csv|html|dat|zip|log|edi|x12)$"
    );


    public String getFilePatternHavingMacros(String destinationAccountId, String agreementId, String destinationChannelId) {
        return macroPatterns.getOrDefault(destinationAccountId, null);
    }

    public Map<String, String> findValidationRuleByDestinationAccountId(String destinationAccountId) {
        // If accountId is valid, return the same map; else return empty.
        return macroPatterns.containsKey(destinationAccountId) ? validationRules : Collections.emptyMap();
    }
}