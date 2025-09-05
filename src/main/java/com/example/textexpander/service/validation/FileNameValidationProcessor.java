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
                log.info("No macro pattern found for agreement {} or channel {}", agreementId, destinationChannelId);
                return true;
            }

            // Get validation rules
            List<FileNameValidationService.ParameterDto> rules = fileNameValidationService.findValidationRuleByDestinationAccountId(destinationAccountId);
            if (rules == null || rules.isEmpty()) {
                log.info("No validation rules for account {}", destinationAccountId);
                return false;
            }

            // Build the global regex pattern
            StringBuilder regexBuilder = new StringBuilder();
            Pattern macroPatternRegex = Pattern.compile("\\$\\{([a-zA-Z0-9_]+)}");
            Matcher m = macroPatternRegex.matcher(macroPattern);

            int lastEnd = 0;
            while (m.find()) {
                // Add static part before macro, always case-insensitive
                if (m.start() > lastEnd) {
                    String staticPart = macroPattern.substring(lastEnd, m.start());
                    regexBuilder.append("(?i:").append(Pattern.quote(staticPart)).append(")");
                }

                String macroName = m.group(1);
                // Find corresponding rule
                FileNameValidationService.ParameterDto rule = rules.stream()
                        .filter(r -> r.getRuleName().equals(macroName))
                        .findFirst()
                        .orElse(null);

                if (rule == null) {
                    log.info("No rule for macro {}", macroName);
                    return false;
                }

                String macroRegex;
                if (rule.getRuleType() == FileNameValidationService.ParameterType.LIST) {
                    // LIST: split, quote, and join case-insensitively
                    String[] options = rule.getRuleValue().split(",");
                    macroRegex = "(?i:" + Arrays.stream(options)
                            .map(String::trim)
                            .map(Pattern::quote)
                            .collect(Collectors.joining("|")) + ")";
                } else if (rule.getRuleType() == FileNameValidationService.ParameterType.REGEX) {
                    // REGEX: insert as-is, remove anchors if present
                    macroRegex = "(" + stripAnchors(rule.getRuleValue()) + ")";
                } else {
                    log.info("Unknown rule type for macro {}", macroName);
                    return false;
                }

                regexBuilder.append(macroRegex);
                lastEnd = m.end();
            }
            // Tail static part
            if (lastEnd < macroPattern.length()) {
                String staticPart = macroPattern.substring(lastEnd);
                regexBuilder.append("(?i:").append(Pattern.quote(staticPart)).append(")");
            }

            String finalRegex = regexBuilder.toString();
            Pattern pattern = Pattern.compile(finalRegex);
            boolean match = pattern.matcher(fileName).matches();
            if (!match) {
                log.info("Filename {} does not match expected pattern {}", fileName, macroPattern);
            }
            return match;
        } catch (Exception e) {
            log.error("Error {}", e.getMessage(), e);
            return false;
        }
    }
    private String stripAnchors(String regex) {
        return regex.replaceAll("^\\^", "").replaceAll("\\$$", "");
    }

//    private boolean validate1(String destinationAccountId, String fileName, String agreementId, String destinationChannelId) {
//        if (fileName == null || fileName.isEmpty()) return false;
//        try {
//            // Get the macro pattern
//            String macroPattern = fileNameValidationService.getFilePatternHavingMacros(destinationAccountId, agreementId, destinationChannelId);
//
//            if (macroPattern == null) {
//                log.info("No macro pattern found for agreement {} or channel {} ", agreementId, destinationChannelId);
//                return true;
//            }
//
//            // Build global regex from macro pattern
//            StringBuilder regexBuilder = new StringBuilder();
//            Matcher m = Pattern.compile("\\$\\{([a-zA-Z0-9_]+)}").matcher(macroPattern);
//            int lastEnd = 0;
//            List<String> groupNames = new ArrayList<>();
//            while (m.find()) {
//                // Static part before macro (case-insensitive)
//                if (m.start() > lastEnd) {
//                    String staticPart = macroPattern.substring(lastEnd, m.start());
//                    // Escape special chars, wrap in (?i:...) for case-insensitivity
//                    regexBuilder.append("(?i:").append(Pattern.quote(staticPart)).append(")");
//                }
//                // Macro group
//                regexBuilder.append("(?<").append(m.group(1)).append(">.+?)");
//                groupNames.add(m.group(1));
//                lastEnd = m.end();
//            }
//            // Tail static part
//            if (lastEnd < macroPattern.length()) {
//                String staticPart = macroPattern.substring(lastEnd);
//                regexBuilder.append("(?i:").append(Pattern.quote(staticPart)).append(")");
//            }
//
//            Pattern pattern = Pattern.compile(regexBuilder.toString());
//            Matcher matcher = pattern.matcher(fileName);
//
//            // If global regex doesn't match with filename, invalid
//            if (!matcher.matches()) {
//                log.info("File name {} does not match pattern {} ", fileName, macroPattern);
//                return false;
//            }
//
//            // Validate each component using rules
//            List<FileNameValidationService.ParameterDto> rules = fileNameValidationService.findValidationRuleByDestinationAccountId(destinationAccountId);
//
//            if (rules == null || rules.isEmpty() || rules.size() != groupNames.size()) {
//                log.info("Rules {} does not exist for components in macro {}", rules, groupNames);
//                return false;
//            }
//
//            for (String groupName : groupNames) {
//                Optional<FileNameValidationService.ParameterDto> ruleOpt = rules.stream()
//                        .filter(r -> r.getRuleName().equals(groupName))
//                        .findFirst();
//
//                if (ruleOpt.isPresent()) {
//                    FileNameValidationService.ParameterDto rule = ruleOpt.get();
//                    String rulePattern = rule.getRuleValue();
//                    String value = matcher.group(groupName);
//
//                    if (rule.getRuleType() == FileNameValidationService.ParameterType.REGEX) {
//                        if (!value.matches(rulePattern)) {
//                            log.info("{} does not match with the rule {}'s regex pattern {} ", value, groupName, rulePattern);
//                            return false;
//                        }
//                    } else if (rule.getRuleType() == FileNameValidationService.ParameterType.LIST) {
//                        // Split by comma (or another delimiter if your pattern uses something else)
//                        boolean isValuePresentInList = Arrays.stream(rulePattern.split(","))
//                                .map(String::trim).anyMatch(s->s.equalsIgnoreCase(value));
//                        if (!isValuePresentInList) {
//                            log.info("{} is not a valid value for rule {}", value, groupName);
//                            return false;
//                        }
//                    } else {
//                        log.info("Unknown ParameterType for groupName: {}", groupName);
//                        return false;
//                    }
//                } else {
//                    log.info("File Rejected: Rule {} does not contains any regex pattern", groupName);
//                    return false;
//                }
//            }
//
//            return true;
//        } catch (Exception e) {
//            log.error("Error {}",e.getMessage(), e);
//            return false;
//        }
//    }
}
