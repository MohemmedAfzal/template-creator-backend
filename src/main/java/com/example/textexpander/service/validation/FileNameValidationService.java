package com.example.textexpander.service.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileNameValidationService {
    // Predefine macros for each accountId
    private static final Map<String, List<MacroEntity>> macroConfig = new HashMap<>();

    static {
        macroConfig.put("ccd.inc", Arrays.asList(
                new MacroEntity("agreementId1", MacroType.AGREEMENT, "<domain>-<filedetails_trackingID>-<sourceSystem>-<version>-<frequency>-<yyyymmddhhmmss>.<fileType>", "ccd.inc"),
                new MacroEntity("inboxId1", MacroType.INBOX, "<domain>-<filedetails_trackingID>-<sourceSystem>-<version>-<frequency>-<yyyymmddhhmmssSSS>.<fileType>", "ccd.inc")
        ));
        macroConfig.put("usha.india", Arrays.asList(
                new MacroEntity("agreementId2", MacroType.AGREEMENT, "<domain>.<filedetails_trackingID>-<sourceSystem>-<version>-<frequency>-<yyyymmddhhmmssSSS>.<fileType>", "usha.india"),
                new MacroEntity("inboxId2", MacroType.INBOX, "<domain>.<fileType>", "usha.india")
        ));
        macroConfig.put("edifecs", Collections.singletonList(
                new MacroEntity("agreementId3", MacroType.AGREEMENT, "<domain>-<filedetails_trackingID>-<sourceSystem>-<version>-<frequency>-<yyyymmddhhmmssSSS>.<fileType>", "edifecs")
        ));
        macroConfig.put("hd.i", Collections.singletonList(
                new MacroEntity("inboxId3", MacroType.INBOX, "<unknown>.<fileType>", "hd.i")
        ));
    }

    // Returns the AGREEMENT macro if exists, otherwise INBOX, otherwise default
    public MacroEntity findMacroByDestinationAccountId(String destinationAccountId) {
        List<MacroEntity> macros = macroConfig.get(destinationAccountId);
        if (macros != null && !macros.isEmpty()) {
            for (MacroEntity macro : macros) {
                if (macro.getMacroType() == MacroType.AGREEMENT) {
                    return macro;
                }
            }
            return macros.get(0);
        }
        // Default macro if none configured
        return null;
    }

    // Validation rules, same rules for all, just different accountId
    public FileValidationEntity findValidationRuleByDestinationAccountId(String destinationAccountId) {
        String config = "[\n" +
                "  {\n" +
                "    \"rule_name\": \"domain\",\n" +
                "    \"rule_type\": 1,\n" +
                "    \"pattern\": \"^(Provider|Member|Reference|Finance|PA|TPL|Claims|Rebate|PAI|FWAFinder)$\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"rule_name\": \"filedetails_trackingID\",\n" +
                "    \"rule_type\": 1,\n" +
                "    \"pattern\": \"^[A-Z0-9]{9}$\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"rule_name\": \"version\",\n" +
                "    \"rule_type\": 1,\n" +
                "    \"pattern\": \"^V[1-9][0-9]{0,2}$\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"rule_name\": \"frequency\",\n" +
                "    \"rule_type\": 1,\n" +
                "    \"pattern\": \"^(Hourly|Daily|Weekly|BiWeekly|Monthly|Quarterly|BiAnnually|Annually|Event|AdHoc)$\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"rule_name\": \"yyyymmddhhmmss\",\n" +
                "    \"rule_type\": 1,\n" +
                "    \"pattern\": \"^\\\\d{14}$\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"rule_name\": \"yyyymmddhhmmssSSS\",\n" +
                "    \"rule_type\": 1,\n" +
                "    \"pattern\": \"^\\\\d{17}$\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"rule_name\": \"fileType\",\n" +
                "    \"rule_type\": 1,\n" +
                "    \"pattern\": \"^\\\\.(txt|xml|csv|html|dat|zip|log|Edi|x12)$\"\n" +
                "  }\n" +
                "]";
        if (macroConfig.containsKey(destinationAccountId)) {
            return new FileValidationEntity(destinationAccountId, config);
        } else {
            return new FileValidationEntity(destinationAccountId, null);
        }
    }

    @Data
    @AllArgsConstructor
    public static class FileValidationEntity{
        private String accountId;
        private String config;
    }

    @Data
    @AllArgsConstructor
    public static class MacroEntity{
        private String id;
        private MacroType macroType;
        private String pattern;
        private String creatorAccountId;
    }
}

enum MacroType {
    INBOX,
    AGREEMENT
}