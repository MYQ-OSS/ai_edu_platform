package top.mayiqin.ai_edu_platform.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 敏感词过滤器（DFA算法）
 * 基于确定有穷自动机实现高效敏感词检测和过滤
 *
 * @author m'y'q
 */
@Slf4j
@Component
public class SensitiveWordFilter {

    /**
     * 敏感词库节点
     */
    private static class TrieNode {
        /** 子节点 */
        private final Map<Character, TrieNode> children = new HashMap<>();
        /** 是否为敏感词结尾 */
        private boolean isEnd = false;
        /** 敏感词 */
        private String word;
    }

    /** 根节点 */
    private final TrieNode rootNode = new TrieNode();

    /** 默认替换符 */
    private static final String DEFAULT_REPLACEMENT = "***";

    /**
     * 初始化敏感词库
     */
    @PostConstruct
    public void init() {
        loadDefaultSensitiveWords();
    }

    /**
     * 加载默认敏感词库
     */
    private void loadDefaultSensitiveWords() {
        List<String> defaultWords = getDefaultSensitiveWords();
        for (String word : defaultWords) {
            addWord(word);
        }
        log.info("已加载 {} 个默认敏感词", defaultWords.size());
    }

    /**
     * 获取默认敏感词列表
     * 包含政治、暴力、色情、违法等类别的敏感词
     */
    private List<String> getDefaultSensitiveWords() {
        return List.of(
            // 暴力类
            "暴力", "血腥", "杀戮", "恐怖", "袭击", "炸弹", "枪击",
            // 色情类
            "色情", "淫秽", "裸体", "性交", "卖淫",
            // 违法类
            "毒品", "赌博", "诈骗", "走私", "贩毒", "洗钱",
            // 政治敏感类
            "恐怖主义", "极端主义", "分裂主义",
            // 侮辱类
            "傻逼", "操你", "去死", "混蛋",
            // 歧视类
            "种族歧视", "性别歧视"
        );
    }

    /**
     * 添加敏感词到词库
     *
     * @param word 敏感词
     */
    public void addWord(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }

        TrieNode currentNode = rootNode;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            TrieNode node = currentNode.children.get(c);
            if (node == null) {
                node = new TrieNode();
                currentNode.children.put(c, node);
            }
            currentNode = node;

            // 标记结尾
            if (i == word.length() - 1) {
                currentNode.isEnd = true;
                currentNode.word = word;
            }
        }
    }

    /**
     * 检测文本是否包含敏感词
     *
     * @param text 待检测文本
     * @return 是否包含敏感词
     */
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (int i = 0; i < text.length(); i++) {
            // 检查以当前位置开始的敏感词
            int sensitiveLength = checkSensitiveWord(text, i);
            if (sensitiveLength > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文本中包含的所有敏感词
     *
     * @param text 待检测文本
     * @return 敏感词列表
     */
    public List<String> getSensitiveWords(String text) {
        List<String> sensitiveWords = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return sensitiveWords;
        }

        for (int i = 0; i < text.length(); i++) {
            int sensitiveLength = checkSensitiveWord(text, i);
            if (sensitiveLength > 0) {
                String word = text.substring(i, i + sensitiveLength);
                sensitiveWords.add(word);
                i += sensitiveLength - 1; // 跳过已检测的字符
            }
        }
        return sensitiveWords;
    }

    /**
     * 过滤文本中的敏感词
     *
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        return filter(text, DEFAULT_REPLACEMENT);
    }

    /**
     * 过滤文本中的敏感词
     *
     * @param text 待过滤文本
     * @param replacement 替换符
     * @return 过滤后的文本
     */
    public String filter(String text, String replacement) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder(text);
        boolean hasSensitiveWord = false;

        for (int i = 0; i < result.length(); i++) {
            int sensitiveLength = checkSensitiveWord(result.toString(), i);
            if (sensitiveLength > 0) {
                // 替换敏感词
                String replacementStr = buildReplacement(replacement, sensitiveLength);
                result.replace(i, i + sensitiveLength, replacementStr);
                hasSensitiveWord = true;
                i += replacement.length() - 1; // 跳过替换符
            }
        }

        return result.toString();
    }

    /**
     * 检查从指定位置开始的敏感词
     *
     * @param text 文本
     * @param startIndex 开始位置
     * @return 敏感词长度，0表示不是敏感词
     */
    private int checkSensitiveWord(String text, int startIndex) {
        TrieNode currentNode = rootNode;
        int sensitiveLength = 0;
        int maxLength = 0;

        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            TrieNode node = currentNode.children.get(c);

            if (node == null) {
                break;
            }

            currentNode = node;
            sensitiveLength++;

            // 记录最长匹配
            if (node.isEnd) {
                maxLength = sensitiveLength;
            }
        }

        return maxLength;
    }

    /**
     * 构建替换字符串
     *
     * @param replacement 替换符
     * @param length 需要替换的长度
     * @return 替换字符串
     */
    private String buildReplacement(String replacement, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(replacement);
        }
        return sb.toString();
    }

    /**
     * 批量添加敏感词
     *
     * @param words 敏感词列表
     */
    public void addWords(List<String> words) {
        if (words != null) {
            words.forEach(this::addWord);
        }
    }

    /**
     * 从文件加载敏感词库
     *
     * @param resourcePath 资源文件路径
     */
    public void loadFromFile(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        addWord(line);
                        count++;
                    }
                }
                log.info("从文件 {} 加载了 {} 个敏感词", resourcePath, count);
            }
        } catch (IOException e) {
            log.warn("加载敏感词文件失败: {}", resourcePath, e);
        }
    }
}
