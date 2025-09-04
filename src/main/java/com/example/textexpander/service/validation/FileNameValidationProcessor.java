package com.example.textexpander.service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileNameValidationProcessor {
    private final FileNameValidationService fileNameValidationService;

    public String process(String fileName, String destinationAccountId) {
        final String sourceAccountId = "mohaafza";
        //final String destinationAccountId = "ccd.inc";
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
        return "Hurray!!!\nIt's a Match:)";
    }

    public boolean validateFileName(String destinationAccountId, String fileName, String destinationChannelId, String agreementId) {
        if (fileName == null || fileName.isEmpty()) return false;
        try {
            return validate(destinationAccountId, fileName, destinationChannelId, agreementId);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validate(String destinationAccountId, String fileName, String agreementId, String destinationChannelId) {
        if (fileName == null || fileName.isEmpty()) return false;
        try {
            // Get the macro pattern
            String macroPattern = fileNameValidationService.getFilePatternHavingMacros(destinationAccountId, agreementId, destinationChannelId);

            if (macroPattern == null) {
                log.info("No macro pattern found for agreement {} or channel {} ", agreementId, destinationChannelId);
                return true;
            }

            // Build global regex from macro pattern
            StringBuilder regexBuilder = new StringBuilder();
            Matcher m = Pattern.compile("<([a-zA-Z0-9_]+)>").matcher(macroPattern);
            int lastEnd = 0;
            List<String> groupNames = new ArrayList<>();
            while (m.find()) {
                regexBuilder.append(Pattern.quote(macroPattern.substring(lastEnd, m.start())));
                regexBuilder.append("(?<").append(m.group(1)).append(">.+?)");
                groupNames.add(m.group(1));
                lastEnd = m.end();
            }
            regexBuilder.append(Pattern.quote(macroPattern.substring(lastEnd)));

            Pattern pattern = Pattern.compile(regexBuilder.toString());
            Matcher matcher = pattern.matcher(fileName);

            // If global regex doesn't match with filename, invalid
            if (!matcher.matches()) {
                log.info("File name {} does not match pattern {} ", fileName, macroPattern);
                return false;
            }

            // Validate each component using rules
            List<FileNameValidationService.ParameterDto> rules = fileNameValidationService.findValidationRuleByDestinationAccountId(destinationAccountId);

            if (rules == null || rules.isEmpty() || rules.size() != groupNames.size()) {
                log.info("Rules {} does not exist for components in macro {}", rules, groupNames);
                return false;
            }

            for (String groupName : groupNames) {
                Optional<FileNameValidationService.ParameterDto> ruleOpt = rules.stream()
                        .filter(r -> r.getAccountId().equals(groupName))
                        .findFirst();

                if (ruleOpt.isPresent()) {
                    FileNameValidationService.ParameterDto rule = ruleOpt.get();
                    String rulePattern = rule.getRegexPattern();
                    String value = matcher.group(groupName);

                    if (rule.getParameterType() == FileNameValidationService.ParameterType.REGEX) {
                        if (!value.matches(rulePattern)) {
                            log.info("{} does not match with the rule {}'s regex pattern {} ", value, groupName, rulePattern);
                            return false;
                        }
                    } else if (rule.getParameterType() == FileNameValidationService.ParameterType.LIST) {
                        // Split by comma (or another delimiter if your pattern uses something else)
                        boolean isValuePresentInList = Arrays.stream(rulePattern.split(","))
                                .map(String::trim).anyMatch(s->s.equalsIgnoreCase(value));
                        if (!isValuePresentInList) {
                            log.info("{} is not a valid value for rule {}", value, groupName);
                            return false;
                        }
                    } else {
                        log.info("Unknown ParameterType for groupName: {}", groupName);
                        return false;
                    }
                } else {
                    log.info("File Rejected: Rule {} does not contains any regex pattern", groupName);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Error {}",e.getMessage(), e);
            return false;
        }
    }
}
