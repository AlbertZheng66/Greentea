package com.xt.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对命令行进行解析，解析规则如下： 1. 参数用空格或者TAB进行分割。 2.
 * 一个字符串若被两个双引号包含，则即使其中包含空格或TAB字符也会被视为一个参数。被引起来的字符串可以嵌入参数内。
 * 字符串中存在的双引号可以通过前置反斜杠进行转义。 3. 反斜杠会被解释成单个字符，除非后面紧接着一个双引号。
 * 4.如果偶数个反斜杠后面跟随一个双引号，每对反斜杠放一个反斜杠到参数中，双引号被解释为一个字符串界定符。
 * 5.如果奇数个反斜杠后面跟随一个双引号，每对反斜杠放一个反斜杠到argv数组中，双引号则被剩下的反斜杠转义放入到argv中，而不会作文字符串界定符。
 *
 * @author Albert
 */
public class CommandLineParser {

    public String[] parse(String cmd) {
        if (org.apache.commons.lang.StringUtils.isEmpty(cmd)) {
            return new String[0];
        }
        char[] cmdChars = cmd.toCharArray();
        int length = cmdChars.length;
        List<String> cmdSegs = new ArrayList();
        StringBuilder cmdSeg = new StringBuilder(); // 当前命令
        boolean dqStarted = false;  // 已经读取一个双引号
        for (int i = 0; i < length; i++) {
            char c = cmdChars[i];
            if (c == '\\') {
                char nextChar = i > cmdChars.length - 2 ? 0 : cmdChars[i + 1];
                if (nextChar == '\\' || nextChar == '"') {
                    cmdSeg.append(nextChar);
                    i++;  // 多移动一个字符
                    continue;
                } else if (nextChar == '\n' || nextChar == '\r') {
                    i++;  // 多移动一个字符
                    continue;
                }
            } else if (dqStarted) {
                if (c == '"') {
                    // 一个命令结束
                    cmdSeg.append(c);
                    appendCmd(cmdSegs, cmdSeg);
                    dqStarted = false;
                    continue;
                }
            } else if ((c == ' ' || c == '\t')) {
                // 一个命令结束
                cmdSeg.append(c);
                appendCmd(cmdSegs, cmdSeg);
                continue;
            } else if (c == '"') {
                dqStarted = true;
            }
            cmdSeg.append(c);
        }
        appendCmd(cmdSegs, cmdSeg); // 将最后的命令补充上
        return cmdSegs.toArray(new String[cmdSegs.size()]);
    }

    private void appendCmd(List<String> cmdSegs, StringBuilder cmdSeg) {
        String str = cmdSeg.toString();
        str = str.trim();
        if (org.apache.commons.lang.StringUtils.isEmpty(str)) {
            return;
        }
        cmdSegs.add(str);
        cmdSeg.delete(0, cmdSeg.length());
    }
}
