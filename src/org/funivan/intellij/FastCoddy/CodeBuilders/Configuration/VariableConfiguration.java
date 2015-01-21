package org.funivan.intellij.FastCoddy.CodeBuilders.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 6/23/14
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariableConfiguration {

    protected String expression;

    protected String defaultValue;

    protected Boolean alwaysStopAt;

    public VariableConfiguration(String expression, String defaultValue, Boolean alwaysStopAt) {
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
