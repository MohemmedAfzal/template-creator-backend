package com.example.textexpander.service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private boolean validate(
            String destinationAccountId,
            String fileName,
            String agreementId,
            String destinationChannelId
    ) throws Exception {
        String macroPattern = fileNameValidationService.getFilePatternHavingMacros(destinationAccountId, agreementId, destinationChannelId);

        if (macroPattern != null) {
            Map<String, String> rules = fileNameValidationService.findValidationRuleByDestinationAccountId(destinationAccountId);

            if (rules == null || rules.isEmpty()) {
                return false;
            }

            try {
                // Build regex matching separators exactly
                List<String> components = new ArrayList<>();
                StringBuilder regexBuilder = new StringBuilder();

                Matcher m = Pattern.compile("<([a-zA-Z0-9_]+)>").matcher(macroPattern);
                int lastEnd = 0;
                while (m.find()) {
                    String sep = macroPattern.substring(lastEnd, m.start());
                    regexBuilder.append(Pattern.quote(sep));
                    String charClass = sep.isEmpty() ? "" : escapeForCharClass(sep);
                    // If separator is empty, allow anything except separator itself (but there is no separator!)
                    regexBuilder.append("(?<").append(m.group(1)).append(">");
                    if (!charClass.isEmpty()) {
                        regexBuilder.append("[^").append(charClass).append("]+");
                    } else {
                        regexBuilder.append(".+"); // fallback: match anything
                    }
                    regexBuilder.append(")");
                    components.add(m.group(1));
                    lastEnd = m.end();
                }
                String trailing = macroPattern.substring(lastEnd);
                regexBuilder.append(Pattern.quote(trailing));

                Pattern macroRegex = Pattern.compile(regexBuilder.toString());
                Matcher fileMatcher = macroRegex.matcher(fileName);

                if (!fileMatcher.matches())
                    return false;

                // Validate each component against rules
                for (String ruleName : rules.keySet()) {
                    String patternStr = rules.get(ruleName);
                    String part = null;
                    try {
                        part = fileMatcher.group(ruleName);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    if (part != null) {
                        String toValidate = ruleName.equals("fileType") ? "." + part : part;
                        Pattern p = Pattern.compile(patternStr);
                        if (!p.matcher(toValidate).matches()) {
                            return false;
                        }
                    }
                }
                return true;
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                return false;
            }
        }
        return true;
    }
    private static String escapeForCharClass(String sep) {
        StringBuilder sb = new StringBuilder();
        for (char c : sep.toCharArray()) {
            if ("\\^-[]".indexOf(c) >= 0) sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }
}
