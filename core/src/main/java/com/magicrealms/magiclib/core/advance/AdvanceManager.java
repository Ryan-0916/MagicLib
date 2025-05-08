package com.magicrealms.magiclib.core.advance;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.Tuple;
import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.advance.entity.AdvanceConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
import net.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
import net.kyori.adventure.text.minimessage.internal.parser.node.ValueNode;
import net.kyori.adventure.text.minimessage.tag.Inserting;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Ryan-0916
 * @Desc 字符偏移度计算
 * @date 2025-05-08
 */
@SuppressWarnings("unused")
public class AdvanceManager {

    private static final Key MINECRAFT_DEFAULT_FONT =
            Key.key("minecraft", "default");

    private final AdvanceLoader advanceLoader;

    private final Cache<String, Integer> cache;

    private final MagicLib plugin;

    public AdvanceManager(MagicLib plugin) {
        this.advanceLoader = new AdvanceLoader(plugin);
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
        this.plugin = plugin;
    }

    public Integer getAdvance(String text) {
        try {
            return cache.get(text, () -> calculateTextAdvance(text));
        } catch (ExecutionException exception) {
            return calculateTextAdvance(text);
        }
    }

    public void clearCache() {
        cache.invalidateAll();
    }

    private Integer calculateTextAdvance(String text) {
        List<Tuple<String, Key, Boolean>>
                iterableTexts = miniMessageToIterable(text);
        int totalAdvance = 0;
        for (Tuple<String, Key, Boolean> element : iterableTexts) {
            AdvanceConfig data = advanceLoader.getAdvanceConfig().get(element.mid());
            if (data == null) {
                plugin.getLoggerManager().warning("未知的字体: " + element.mid() + " 请添加到 Advance.yml");
                continue;
            }
            char[] chars = element.left().toCharArray();
            for (int j = 0; j < chars.length; j++) {
                int advance;
                if (Character.isHighSurrogate(chars[j])) {
                    advance = data.getCharWidthMap().getOrDefault(
                            Character.toCodePoint(chars[j], chars[++j]),
                            data.getDefaultWidth()
                    );
                } else {
                    advance = data.getCharWidthMap().getOrDefault(
                            (int) chars[j],
                            data.getDefaultWidth()
                    );
                }
                totalAdvance += advance;
                if (element.right()) {
                    totalAdvance += 1;
                }
            }
        }
        return totalAdvance;
    }

    @SuppressWarnings("UnstableApiUsage")
    public List<Tuple<String, Key, Boolean>> miniMessageToIterable(String text) {
        if (AdventureHelper.legacySupport) text =
                AdventureHelper.legacyToMiniMessage(text);
        ElementNode node = (ElementNode) AdventureHelper.getMiniMessage()
                .deserializeToTree(text);
        List<Tuple<String, Key, Boolean>> iterableTexts = new ObjectArrayList<>();
        nodeToIterableTexts(node, iterableTexts, MINECRAFT_DEFAULT_FONT, false);
        return iterableTexts;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void nodeToIterableTexts(ElementNode node, List<Tuple<String, Key, Boolean>> list, Key font, boolean bold) {
        if (node instanceof ValueNode valueNode) {
            String text = valueNode.value();
            if (!text.isEmpty())
                list.add(Tuple.of(text, font, bold));
        } else if (node instanceof TagNode tagNode) {
            if (tagNode.tag() instanceof Inserting inserting) {
                Component component = inserting.value();
                switch (component.decoration(TextDecoration.BOLD)) {
                    case TRUE -> bold = true;
                    case FALSE -> bold = false;
                    case NOT_SET -> {}
                }
                Key key = component.font();
                if (key != null)
                    font = key;
                if (component instanceof TextComponent textComponent) {
                    String text = textComponent.content();
                    if (!text.isEmpty())
                        list.add(Tuple.of(text, font, bold));
                }
            }
        }
        if (!node.unsafeChildren().isEmpty()) {
            for (ElementNode child : node.unsafeChildren()) {
                this.nodeToIterableTexts(child, list, font, bold);
            }
        }
    }

}
