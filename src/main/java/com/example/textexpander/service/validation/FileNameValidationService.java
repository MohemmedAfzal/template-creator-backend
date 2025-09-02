package com.example.textexpander.service.validation;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileNameValidationService {

    private static final Map<String, String> macroPatterns = Map.of(
            "ccd.inc", "<domain>-<filedetailstrackingId>-<sourceSystem>-<version>-<frequency>-<yyyymmddhhmmss>.<fileType>",
            "usha.india", "<domain>.<filedetailstrackingId>-<sourceSystem>-<version>-<frequency>-<yyyymmddhhmmss>.<fileType>",
            "edifecs", "<domain>ggh-<fileTrackingDetailsRSP>.<sourceSystem>_<version>-<frequency>_<yyyymmddhhmmssSSS>.<fileType>",
            "hd.i", "<unknown>.<fileType>"
    );


    private static final Map<String, Map<String, String>> validationRules = Map.of("ccd.inc", Map.of(
            "domain", "^(?i)(Provider|Member|Reference|Finance|PA|TPL|Claims|Rebate|PAI|FWAFinder)$",
            "filedetailstrackingId", "^(?i)[A-Z0-9_]+_[A-Z0-9_]{9}",
            "sourceSystem", "^(?i)(PBMS|EDI|MMIS|FWAFinder|EDW)$",
            "version", "^(?i)V[1-9][0-9]{0,2}$",
            "frequency", "^(?i)(Hourly|Daily|Weekly|BiWeekly|Monthly|Quarterly|BiAnnually|Annually|Event|AdHoc)$",
            "yyyymmddhhmmss", "^\\d{14}$",
            "fileType", "^(?i)(txt|xml|csv|html|dat|zip|log|edi|x12)$"
    ),
    "usha.india", Map.of(
                    "domain", "^(?i)(Provider|Member|Reference|Finance|PA|TPL|Claims|Rebate|PAI|FWAFinder)$",
                    "filedetailstrackingId", "^(?i)[A-Z0-9_]+_[A-Z0-9_]{9}",
                    "sourceSystem", "^(?i)(PBMS|EDI|MMIS|FWAFinder|EDW)$",
                    "version", "^(?i)V[1-9][0-9]{0,2}$",
                    "frequency", "^(?i)(Hourly|Daily|Weekly|BiWeekly|Monthly|Quarterly|BiAnnually|Annually|Event|AdHoc)$",
                    "yyyymmddhhmmssSSS", "^\\d{17}$",
                    "fileType", "^(?i)(txt|xml|csv|html|dat|zip|log|edi|x12)$"
            ),
            "edifecs", Map.of(
                    "domain", "^(?i)(Provider|Member|Reference|Finance|PA|TPL|Claims|Rebate|PAI|FWAFinder)$",
                    //"filedetailstrackingId", "^(?i)[A-Z0-9_]+_[A-Z0-9_]{9}+_{rsp}",
                    "fileTrackingDetailsRSP", "^(?i)[A-Z0-9_]+_[A-Z0-9_]{9}_rsp$",
                    "sourceSystem", "^(?i)(PBMS|EDI|MMIS|FWAFinder|EDW)$",
                    "version", "^(?i)V[1-9][0-9]{0,2}$",
                    "frequency", "^(?i)(Hourly|Daily|Weekly|BiWeekly|Monthly|Quarterly|BiAnnually|Annually|Event|AdHoc)$",
                    "yyyymmddhhmmssSSS", "^\\d{17}$",
                    "fileType", "^(?i)(txt|xml|csv|html|dat|zip|log|edi|x12)$"
            )
    );


    public String getFilePatternHavingMacros(String destinationAccountId, String agreementId, String destinationChannelId) {
        return macroPatterns.getOrDefault(destinationAccountId, null);
    }

    public Map<String, String> findValidationRuleByDestinationAccountId(String destinationAccountId) {
        // If accountId is valid, return the same map; else return empty.
        return macroPatterns.containsKey(destinationAccountId) ? validationRules.get(destinationAccountId) : Collections.emptyMap();
    }
}