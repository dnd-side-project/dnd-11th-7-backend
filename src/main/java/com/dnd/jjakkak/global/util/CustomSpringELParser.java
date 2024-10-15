package com.dnd.jjakkak.global.util;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 분산락 AOP에서 사용하는 커스텀 스프링 표현식 Parser입니다.
 *
 * @author 류태웅
 * @version 2024. 10. 15.
 */

public class CustomSpringELParser {
    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key){
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for(int i=0; i<parameterNames.length; i++){
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, Object.class);
    }
}
