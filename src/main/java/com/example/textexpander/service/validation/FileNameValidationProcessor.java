package com.example.textexpander.service.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class FileNameValidationProcessor {
    private final FileNameValidationService fileNameValidationService;

    public String process(String fileName) {
        final String sourceAccountId = "mohaafza";
        final String destinationAccountId = "ccd.inc";
        final String destinationChannelId = "inboxId1";
        final String agreementId = "agreementId1";

        //if (agreementId != null) {
            boolean isValidationSuccess = validateFileName(destinationAccountId, fileName, destinationChannelId, agreementId);
            if (!isValidationSuccess) {
                return "Filename did not match with agreement pattern";
            }
//        } else if (destinationChannelId != null) {
//            boolean isValidationSuccess = validateFileName(destinationAccountId, fileName, destinationChannelId, null);
//            if (!isValidationSuccess) {
//                return "Filename did not match with inbox pattern";
//            }
//        }
        return null;
    }

    public boolean validateFileName(String destinationAccountId, String fileName, String destinationChannelId, String agreementId) {
        if (fileName == null || fileName.isEmpty()) return false;
        try {
            return validate(destinationAccountId, fileName, destinationChannelId, agreementId);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validate(String destinationAccountId, String fileName, String destinationChannelId, String agreementId) {
        FileNameValidationService.MacroEntity macro = fileNameValidationService.findMacroByDestinationAccountId(destinationAccountId);
        if (macro != null && destinationAccountId.equalsIgnoreCase(macro.getCreatorAccountId())
                && (agreementId.equalsIgnoreCase(macro.getId())
                || destinationChannelId.equalsIgnoreCase(Objects.requireNonNull(macro).getId()))) {

            FileNameValidationService.FileValidationEntity ruleEntity = fileNameValidationService.findValidationRuleByDestinationAccountId(destinationAccountId);

            if (ruleEntity == null || ruleEntity.getConfig() == null) {
                return false; //TODO: check whether we need to return true or false if rule not found for particular macro
            }

            try {
                // Parse rules JSON
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> rules = mapper.readValue(ruleEntity.getConfig(), new TypeReference<List<Map<String, Object>>>() {
                });

                // Build regex for macro pattern, replace <component> with (.+)
                String macroPattern = macro.getPattern();
                List<String> components = new ArrayList<>();
                Matcher m = Pattern.compile("<([a-zA-Z0-9_]+)>").matcher(macroPattern);

                StringBuilder regexBuilder = new StringBuilder();
                int lastEnd = 0;
                while (m.find()) {
                    regexBuilder.append(Pattern.quote(macroPattern.substring(lastEnd, m.start())));
                    regexBuilder.append("(?<").append(m.group(1)).append(">.+)");
                    components.add(m.group(1));
                    lastEnd = m.end();
                }
                regexBuilder.append(Pattern.quote(macroPattern.substring(lastEnd)));

                Pattern macroRegex = Pattern.compile(regexBuilder.toString());
                Matcher fileMatcher = macroRegex.matcher(fileName);

                if (!fileMatcher.matches()) return false;

                // Validate each component with its rule
                for (Map<String, Object> rule : rules) {
                    String ruleName = (String) rule.get("rule_name");
                    String patternStr = (String) rule.get("pattern");
                    if (fileMatcher.group(ruleName) != null) {
                        String part = fileMatcher.group(ruleName);
                        Pattern p = Pattern.compile(patternStr);
                        if (!p.matcher(part).matches()) {
                            return false; // component failed validation
                        }
                    }
                }
                return true; // all validations passed
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return false;
            }
        }
        return true;
    }
}
