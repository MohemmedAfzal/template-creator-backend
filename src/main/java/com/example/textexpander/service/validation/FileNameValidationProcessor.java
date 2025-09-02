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
    ) {
        // 1. Get the macro pattern
        String macroPattern = fileNameValidationService.getFilePatternHavingMacros(
                destinationAccountId, agreementId, destinationChannelId);

        if (macroPattern == null) {
            // No pattern to validate against, accept any file name
            return true;
        }

        // 2. Build regex from macro pattern
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

        // 3. If regex doesn't match, invalid
        if (!matcher.matches()) {
            return false;
        }

        // 4. (Optional) Validate each component using rules
        Map<String, String> rules = fileNameValidationService.findValidationRuleByDestinationAccountId(destinationAccountId);//TODO: check if rules is empty
        if (rules != null && !rules.isEmpty()) {
            for (String groupName : groupNames) {
                String ruleRegex = rules.get(groupName);
                if (ruleRegex != null) {
                    String value = matcher.group(groupName);
                    if (!value.matches(ruleRegex)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
    private static String escapeForCharClass(String sep) {
        // Escape only char-class special characters
        StringBuilder sb = new StringBuilder();
        for (char c : sep.toCharArray()) {
            if ("\\^-[]".indexOf(c) >= 0) sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }
}
