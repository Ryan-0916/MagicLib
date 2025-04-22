package com.magicrealms.magiclib.common.message.helper;

import com.magicrealms.magiclib.common.utils.StringUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TypewriterHelper {

    private final String prefix;
    private final String suffix;
    @Getter
    private final String realContent;
    private final Map<Integer, String> tagStartMap;
    private final StringBuilder currentText = new StringBuilder();
    private int index;

    public TypewriterHelper(String content, @Nullable String prefix, @Nullable String suffix) {
        this.prefix = AdventureHelper.legacyToMiniMessage(StringUtils.defaultString(prefix));
        this.suffix = AdventureHelper.legacyToMiniMessage(StringUtils.defaultString(suffix));
        content = AdventureHelper.legacyToMiniMessage(content);
        this.realContent = extractRealContent(content);
        this.tagStartMap = buildTagStartMap(content);
    }

    private String extractRealContent(String content) {
        List<String> tags = StringUtil.getTagsToList(content);
        return tags.stream()
                .map(tag -> tag.split("::", 2))
                .filter(parts -> parts.length > 1)
                .map(parts -> parts[1])
                .collect(Collectors.joining());
    }

    private Map<Integer, String> buildTagStartMap(String content) {
        List<String> tags = StringUtil.getTagsToList(content);
        Map<Integer, String> map = new HashMap<>();
        int currentPosition = 0;
        for (String tag : tags) {
            String[] parts = tag.split("::", 2);
            if (parts.length < 2) continue;
            String tagContent = parts[1];
            if (!tagContent.isEmpty()) {
                map.put(currentPosition, parts[0]);
                currentPosition += tagContent.length();
            }
        }
        return map;
    }

    public boolean isPrinted() {
        return index >= realContent.length();
    }

    public String print() {
        if (isPrinted()) {
            return buildFinalText();
        }
        String tagName = tagStartMap.get(index);
        if (tagName != null && !tagName.equals("prefix")) {
            currentText.append("<").append(tagName).append(">");
        }
        currentText.append(realContent.charAt(index));
        index++;
        return buildFinalText();
    }

    private String buildFinalText() {
        String ellipsis = isPrinted() ? "" : "...";
        return prefix + currentText + ellipsis + suffix;
    }
}