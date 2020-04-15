package org.funivan.intellij.FastCoddy.CodeBuilders.Configuration;

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
public class VariableConfiguration {

    private String expression;

    private String defaultValue;

    private Boolean alwaysStopAt;

    VariableConfiguration(String expression, String defaultValue, Boolean alwaysStopAt) {
        this.expression = expression;
        this.defaultValue = defaultValue;
        this.alwaysStopAt = alwaysStopAt;
    }

    public Boolean getAlwaysStopAt() {
        return alwaysStopAt;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getExpression() {
        return expression;
    }
}
