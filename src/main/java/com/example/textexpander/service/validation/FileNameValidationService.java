package com.example.textexpander.service.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileNameValidationService {

private static final Map<String, String> macroPatterns = Map.of(
        "ccd.inc", "${domain}-somehardcodedvalue-${filedetailstrackingId}-${sourceSystem}-${version}-${frequency}-${yyyymmddhhmmss}.${fileType}_HardCodedValue",
        "usha.india", "${domain}.${filedetailstrackingId}-${sourceSystem}-${version}-${frequency}-${yyyymmddhhmmss}.${fileType}",
        "edifecs", "${domain}-${fileTrackingDetails}_rsp-${sourceSystem}_${version}-${frequency}_${yyyymmddhhmmssSSS}.${fileType}",
        "hd.i", "${unknown}.${fileType}"
);


    private static final Map<String, List<ParameterDto>> validationRules = Map.of(
            "ccd.inc", List.of(
                    new ParameterDto("domain", ParameterType.LIST, "Provider,Member,Reference,Finance,PA,TPL,Claims,Rebate,PAI,FWAFinder"),
                    new ParameterDto("filedetailstrackingId", ParameterType.REGEX, "^(?i)[A-Z0-9_]+_[A-Z0-9_]{9}"),
                    new ParameterDto("sourceSystem", ParameterType.LIST, "PBMS,EDI,MMIS,FWAFinder,EDW"),
                    new ParameterDto("version", ParameterType.REGEX, "^(?i)V[1-9][0-9]{0,2}$"),
                    new ParameterDto("frequency", ParameterType.LIST, "Hourly,Daily,Weekly,BiWeekly,Monthly,Quarterly,BiAnnually,Annually,Event,AdHoc"),
                    new ParameterDto("yyyymmddhhmmss", ParameterType.REGEX, "^\\d{14}$"),
                    new ParameterDto("fileType", ParameterType.LIST, "txt,xml,csv,html,dat,zip,log,edi,x12")
            ),
            "usha.india", List.of(
                    new ParameterDto("domain", ParameterType.LIST, "Provider,Member,Reference,Finance,PA,TPL,Claims,Rebate,PAI,FWAFinder"),
                    new ParameterDto("filedetailstrackingId", ParameterType.REGEX, "^(?i)[A-Z0-9_]+_[A-Z0-9_]{9}"),
                    new ParameterDto("sourceSystem", ParameterType.LIST, "PBMS,EDI,MMIS,FWAFinder,EDW"),
                    new ParameterDto("version", ParameterType.LIST, "V1,V2,V3,V4,V5,V6,V7,V8,V9,V10"), // Example: only allow version up to V10
                    new ParameterDto("frequency", ParameterType.REGEX, "^(?i)(Hourly|Daily|Weekly|BiWeekly|Monthly|Quarterly|BiAnnually|Annually|Event|AdHoc)$"),
                    new ParameterDto("yyyymmddhhmmssSSS", ParameterType.REGEX, "^\\d{17}$"),
                    new ParameterDto("fileType", ParameterType.LIST, "txt,xml,csv,html,dat,zip,log,edi,x12")
            ),
            "edifecs", List.of(
                    new ParameterDto("domain", ParameterType.LIST, "Provider,Member,Reference,Finance,PA,TPL,Claims,Rebate,PAI,FWAFinder"),
                    new ParameterDto("fileTrackingDetails", ParameterType.REGEX, "^(?i)[A-Z0-9_]+_[A-Z0-9_]{9}$"),
                    new ParameterDto("sourceSystem", ParameterType.LIST, "PBMS,EDI,MMIS,FWAFinder,EDW"),
                    new ParameterDto("version", ParameterType.LIST, "V1,V2,V3,V4,V5,V6,V7,V8,V9,V10"), // Example: only allow version up to V10
                    new ParameterDto("frequency", ParameterType.LIST, "Hourly,Daily,Weekly,BiWeekly,Monthly,Quarterly,BiAnnually,Annually,Event,AdHoc"),
                    new ParameterDto("yyyymmddhhmmssSSS", ParameterType.REGEX, "^\\d{17}$"),
                    new ParameterDto("fileType", ParameterType.LIST, "txt,xml,csv,html,dat,zip,log,edi,x12")
            )
    );


    public String getFilePatternHavingMacros(String destinationAccountId, String agreementId, String destinationChannelId) {
        return macroPatterns.getOrDefault(destinationAccountId, null);
    }

    public List<ParameterDto> findValidationRuleByDestinationAccountId(String destinationAccountId) {
        return macroPatterns.containsKey(destinationAccountId)
                ? validationRules.getOrDefault(destinationAccountId, Collections.emptyList())
                : Collections.emptyList();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ParameterDto{
        private String ruleName;
        private ParameterType ruleType;
        private String ruleValue;
    }
    enum ParameterType{
        LIST,
        REGEX
    }
}