package com.dnd.jjakkak.global.util;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 어노테이션에 작성된 Spring EL 표현식을 파싱하는 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 10. 15.
 */
public class CustomSpringELParser {

    private CustomSpringELParser() {
        throw new IllegalStateException("Utility class");
    }

    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, Object.class);
    }
}
