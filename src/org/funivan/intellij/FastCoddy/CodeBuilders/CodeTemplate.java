package org.funivan.intellij.FastCoddy.CodeBuilders;

import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.VariableConfiguration;

import java.util.HashMap;

/**
 * Final configuration of template
 * Contain expanded template, variables configuration and some statistic data (usedShortCodesNum and initialString)
 * <p/>
 * User: ivan
 * Date: 6/23/14
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CodeTemplate {

    protected String initialString = "";

    protected Integer usedShortCodesNum = 0;

    protected String code;

    protected HashMap<String, VariableConfiguration> variablesConfiguration;

    public CodeTemplate(String code, HashMap<String, VariableConfiguration> variablesConfiguration) {
        this.code = code;
        this.variablesConfiguration = variablesConfiguration;
    }

    public String getCode() {
        return code;
    }

    public Boolean isEmpty() {
        return code.isEmpty();
    }


    public HashMap<String, VariableConfiguration> getVariablesConfiguration() {
        return variablesConfiguration;
    }

    public String getInitialString() {
        return initialString;
    }

    public void setInitialString(String initialString) {
        this.initialString = initialString;
    }

    public Integer getUsedShortCodesNum() {
        return usedShortCodesNum;
    }

    public void setUsedShortCodesNum(Integer usedShortCodesNum) {
        this.usedShortCodesNum = usedShortCodesNum;
    }
}
