package com.nj.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 班组分类规则引擎
 * 用于自动解析Excel公式并执行一次班组判定
 *
 * @author:
 * @data: 2026/3/25
 */
@Slf4j
public class TeamClassifyRuleEngine {

    /**
     * 规则节点类型
     */
    public enum NodeType {
        IF,         // IF条件
        AND,        // AND条件
        OR,         // OR条件
        NOT,        // NOT条件
        EQUALS,     // 等于比较
        CONTAINS,   // 包含判断
        NOT_CONTAINS, // 不包含判断
        STRING,     // 字符串常量
        REFERENCE,  // 单元格引用
        RETURN      // 返回结果
    }

    /**
     * 规则节点
     */
    @Data
    public static class RuleNode {
        private NodeType type;
        private String value;
        private List<RuleNode> children;
        private RuleNode trueBranch;   // IF为真时的分支
        private RuleNode falseBranch;  // IF为假时的分支

        public RuleNode(NodeType type) {
            this.type = type;
            this.children = new ArrayList<>();
        }

        public RuleNode(NodeType type, String value) {
            this.type = type;
            this.value = value;
            this.children = new ArrayList<>();
        }
    }

    /**
     * 解析Excel公式为规则树
     *
     * @param formula Excel公式
     * @return 规则树根节点
     */
    public static RuleNode parseFormula(String formula) {
        if (formula.isEmpty()) {
            return null;
        }
        
        // 移除开头的等号
        String expr = formula.trim();
        if (expr.startsWith("=")) {
            expr = expr.substring(1);
        }
        
        try {
            return parseExpression(expr);
        } catch (Exception e) {
            log.error("解析公式失败: {}, 错误: {}", formula, e.getMessage());
            return null;
        }
    }

    /**
     * 递归解析表达式
     */
    private static RuleNode parseExpression(String expr) {
        expr = expr.trim();
        
        // 处理IF函数
        if (expr.toUpperCase().startsWith("IF(")) {
            return parseIf(expr);
        }
        
        // 处理AND函数
        if (expr.toUpperCase().startsWith("AND(")) {
            return parseAnd(expr);
        }
        
        // 处理OR函数
        if (expr.toUpperCase().startsWith("OR(")) {
            return parseOr(expr);
        }
        
        // 处理NOT函数
        if (expr.toUpperCase().startsWith("NOT(")) {
            return parseNot(expr);
        }
        
        // 处理ISNUMBER(FIND(...))
        if (expr.toUpperCase().startsWith("ISNUMBER(FIND(")) {
            return parseIsNumberFind(expr);
        }
        
        // 处理NOT(ISNUMBER(FIND(...)))
        if (expr.toUpperCase().startsWith("NOT(ISNUMBER(FIND(")) {
            return parseNotIsNumberFind(expr);
        }
        
        // 处理等号比较 (如 N3="国网九江供电公司")
        if (expr.contains("=") && !expr.contains("(") && !expr.contains(")")) {
            return parseEquals(expr);
        }
        
        // 处理字符串常量
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return new RuleNode(NodeType.STRING, expr.substring(1, expr.length() - 1));
        }
        
        // 处理变量名引用 (如 cityName, crewName)
        // 支持字母开头的变量名
        if (expr.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            return new RuleNode(NodeType.REFERENCE, expr);
        }
        
        return new RuleNode(NodeType.STRING, expr);
    }

    /**
     * 解析IF函数
     * IF(条件, 真值, 假值)
     */
    private static RuleNode parseIf(String expr) {
        String content = extractContent(expr, "IF");
        List<String> args = splitArgs(content);
        
        if (args.size() != 3) {
            log.error("IF函数参数错误: {}", expr);
            return null;
        }
        
        RuleNode node = new RuleNode(NodeType.IF);
        node.children.add(parseExpression(args.get(0))); // 条件
        node.trueBranch = parseReturnValue(args.get(1)); // 真值
        node.falseBranch = parseReturnValue(args.get(2)); // 假值
        
        return node;
    }

    /**
     * 解析AND函数
     */
    private static RuleNode parseAnd(String expr) {
        String content = extractContent(expr, "AND");
        List<String> args = splitArgs(content);
        
        RuleNode node = new RuleNode(NodeType.AND);
        for (String arg : args) {
            node.children.add(parseExpression(arg.trim()));
        }
        return node;
    }

    /**
     * 解析OR函数
     */
    private static RuleNode parseOr(String expr) {
        String content = extractContent(expr, "OR");
        List<String> args = splitArgs(content);
        
        RuleNode node = new RuleNode(NodeType.OR);
        for (String arg : args) {
            node.children.add(parseExpression(arg.trim()));
        }
        return node;
    }

    /**
     * 解析NOT函数
     */
    private static RuleNode parseNot(String expr) {
        String content = extractContent(expr, "NOT");
        RuleNode node = new RuleNode(NodeType.NOT);
        node.children.add(parseExpression(content));
        return node;
    }

    /**
     * 解析ISNUMBER(FIND(...))
     * 表示"包含"关系
     */
    private static RuleNode parseIsNumberFind(String expr) {
        // 提取 FIND(...) 部分
        String findExpr = expr.substring(expr.toUpperCase().indexOf("FIND("));
        String content = extractContent(findExpr, "FIND");
        List<String> args = splitArgs(content);
        
        if (args.size() != 2) {
            return null;
        }
        
        RuleNode node = new RuleNode(NodeType.CONTAINS);
        node.children.add(parseExpression(args.get(1))); // 被查找的字符串
        node.children.add(parseExpression(args.get(0))); // 查找的子串
        return node;
    }

    /**
     * 解析NOT(ISNUMBER(FIND(...)))
     * 表示"不包含"关系
     */
    private static RuleNode parseNotIsNumberFind(String expr) {
        // 提取 FIND(...) 部分
        String findExpr = expr.substring(expr.toUpperCase().indexOf("FIND("));
        String content = extractContent(findExpr, "FIND");
        List<String> args = splitArgs(content);
        
        if (args.size() != 2) {
            return null;
        }
        
        RuleNode node = new RuleNode(NodeType.NOT_CONTAINS);
        node.children.add(parseExpression(args.get(1))); // 被查找的字符串
        node.children.add(parseExpression(args.get(0))); // 查找的子串
        return node;
    }

    /**
     * 解析等号比较
     */
    private static RuleNode parseEquals(String expr) {
        int eqIndex = expr.indexOf('=');
        if (eqIndex > 0) {
            RuleNode node = new RuleNode(NodeType.EQUALS);
            node.children.add(parseExpression(expr.substring(0, eqIndex).trim()));
            node.children.add(parseExpression(expr.substring(eqIndex + 1).trim()));
            return node;
        }
        return null;
    }

    /**
     * 解析返回值
     */
    private static RuleNode parseReturnValue(String expr) {
        expr = expr.trim();
        // 如果是嵌套的IF，继续解析
        if (expr.toUpperCase().startsWith("IF(")) {
            return parseIf(expr);
        }
        // 否则作为字符串结果
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return new RuleNode(NodeType.RETURN, expr.substring(1, expr.length() - 1));
        }
        return new RuleNode(NodeType.RETURN, expr);
    }

    /**
     * 提取函数内容
     */
    private static String extractContent(String expr, String funcName) {
        String prefix = funcName + "(";
        int start = expr.toUpperCase().indexOf(prefix.toUpperCase());
        if (start >= 0) {
            int end = findMatchingParen(expr, start + prefix.length() - 1);
            if (end > 0) {
                return expr.substring(start + prefix.length(), end);
            }
        }
        return "";
    }

    /**
     * 找到匹配的右括号
     */
    private static int findMatchingParen(String expr, int start) {
        int depth = 1;
        for (int i = start + 1; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') {
                depth++;
            } else if (expr.charAt(i) == ')') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 分割参数（处理嵌套）
     */
    private static List<String> splitArgs(String content) {
        List<String> args = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        
        for (char c : content.toCharArray()) {
            if (c == '(') {
                depth++;
                current.append(c);
            } else if (c == ')') {
                depth--;
                current.append(c);
            } else if (c == ',' && depth == 0) {
                args.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            args.add(current.toString().trim());
        }
        
        return args;
    }

    /**
     * 执行规则判定
     *
     * @param root 规则树根节点
     * @param context 上下文数据
     * @return 判定结果字符串
     */
    public static String evaluate(RuleNode root, Map<String, Object> context) {
        if (root == null) {
            return "其他班组";
        }
        return evaluateNode(root, context);
    }

    /**
     * 递归执行节点
     */
    private static String evaluateNode(RuleNode node, Map<String, Object> context) {
        if (node == null) {
            return "其他班组";
        }
        
        switch (node.type) {
            case IF:
                boolean condition = evaluateCondition(node.children.get(0), context);
                if (condition) {
                    return evaluateNode(node.trueBranch, context);
                } else {
                    return evaluateNode(node.falseBranch, context);
                }
                
            case AND:
                for (RuleNode child : node.children) {
                    if (!evaluateCondition(child, context)) {
                        return evaluateNode(new RuleNode(NodeType.RETURN, "其他班组"), context);
                    }
                }
                return "一次班组";
                
            case OR:
                for (RuleNode child : node.children) {
                    if (evaluateCondition(child, context)) {
                        return "一次班组";
                    }
                }
                return "其他班组";
                
            case RETURN:
                return node.value;
                
            default:
                return node.value != null ? node.value : "其他班组";
        }
    }

    /**
     * 执行条件判断
     */
    private static boolean evaluateCondition(RuleNode node, Map<String, Object> context) {
        if (node == null) {
            return false;
        }
        
        switch (node.type) {
            case EQUALS:
                String left = getValue(node.children.get(0), context);
                String right = getValue(node.children.get(1), context);
                return Objects.equals(left, right);
                
            case CONTAINS:
                String text = getValue(node.children.get(0), context);
                String search = getValue(node.children.get(1), context);
                return text != null && text.contains(search);
                
            case NOT_CONTAINS:
                String text2 = getValue(node.children.get(0), context);
                String search2 = getValue(node.children.get(1), context);
                return text2 == null || !text2.contains(search2);
                
            case AND:
                for (RuleNode child : node.children) {
                    if (!evaluateCondition(child, context)) {
                        return false;
                    }
                }
                return true;
                
            case OR:
                for (RuleNode child : node.children) {
                    if (evaluateCondition(child, context)) {
                        return true;
                    }
                }
                return false;
                
            case NOT:
                return !evaluateCondition(node.children.get(0), context);
                
            default:
                return false;
        }
    }

    /**
     * 获取节点值
     */
    private static String getValue(RuleNode node, Map<String, Object> context) {
        if (node == null) {
            return "";
        }
        
        switch (node.type) {
            case STRING:
                return node.value;
            case REFERENCE:
                Object val = context.get(node.value);
                return val != null ? val.toString() : "";
            default:
                return node.value != null ? node.value : "";
        }
    }

    /**
     * 判定是否为一次班组（便捷方法）
     *
     * @param formula Excel公式
     * @param cityName 地市单位名称
     * @param crewName 班组名称
     * @return true: 一次班组, false: 其他班组
     */
    public static boolean isFirstClassTeam(String formula, String cityName, String crewName) {
        RuleNode root = parseFormula(formula);
        Map<String, Object> context = new HashMap<>();
        context.put("cityName", cityName);
        context.put("crewName", crewName);
        
        String result = evaluate(root, context);
        return "一次班组".equals(result);
    }

    /**
     * 打印规则树（用于调试）
     */
    public static void printRuleTree(RuleNode node, int indent) {
        if (node == null) {
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        sb.append(node.type);
        if (node.value != null) {
            sb.append(" [").append(node.value).append("]");
        }
        System.out.println(sb.toString());
        
        for (RuleNode child : node.children) {
            printRuleTree(child, indent + 1);
        }
        
        if (node.trueBranch != null) {
            printRuleTree(node.trueBranch, indent + 2);
        }
        
        if (node.falseBranch != null) {
            printRuleTree(node.falseBranch, indent + 2);
        }
    }

    /**
     * 使用示例
     */
    public static void main(String[] args) {
        // Excel公式 (使用语义化变量名 cityName 和 crewName)
        String formula = "=IF(AND(cityName=\"国网九江供电公司\",crewName=\"变电检修三班\"),\"其他班组\",IF(AND(cityName=\"国网江西超高压公司\",NOT(ISNUMBER(FIND(\"一次\",crewName))),OR(ISNUMBER(FIND(\"变电运检\",crewName)),ISNUMBER(FIND(\"特高压运检班\",crewName)))),\"其他班组\",IF(crewName=\"变电修试及二次系统及数字化班\",\"一次班组\",IF(AND(NOT(ISNUMBER(FIND(\"二次\",crewName))),OR(ISNUMBER(FIND(\"检\",crewName)),ISNUMBER(FIND(\"修\",crewName)),ISNUMBER(FIND(\"试\",crewName)))),\"一次班组\",\"其他班组\"))))";
        
        System.out.println("=== 解析公式 ===");
        RuleNode root = parseFormula(formula);
        printRuleTree(root, 0);
        
        System.out.println("\n=== 测试判定 ===");
        
        // 测试用例1: 国网九江供电公司 + 变电检修三班 → 其他班组
        boolean result1 = isFirstClassTeam(formula, "国网九江供电公司", "变电检修三班");
        System.out.println("国网九江供电公司 + 变电检修三班: " + (result1 ? "一次班组" : "其他班组") + " (期望: 其他班组)");
        
        // 测试用例2: 国网江西超高压公司 + 变电运检一班 → 其他班组
        boolean result2 = isFirstClassTeam(formula, "国网江西超高压公司", "变电运检一班");
        System.out.println("国网江西超高压公司 + 变电运检一班: " + (result2 ? "一次班组" : "其他班组") + " (期望: 其他班组)");
        
        // 测试用例3: 变电修试及二次系统及数字化班 → 一次班组
        boolean result3 = isFirstClassTeam(formula, "国网南昌供电公司", "变电修试及二次系统及数字化班");
        System.out.println("变电修试及二次系统及数字化班: " + (result3 ? "一次班组" : "其他班组") + " (期望: 一次班组)");
        
        // 测试用例4: 变电检修一班 → 一次班组
        boolean result4 = isFirstClassTeam(formula, "国网南昌供电公司", "变电检修一班");
        System.out.println("变电检修一班: " + (result4 ? "一次班组" : "其他班组") + " (期望: 一次班组)");
        
        // 测试用例5: 二次检修班 → 其他班组
        boolean result5 = isFirstClassTeam(formula, "国网南昌供电公司", "二次检修班");
        System.out.println("二次检修班: " + (result5 ? "一次班组" : "其他班组") + " (期望: 其他班组)");
    }
}
