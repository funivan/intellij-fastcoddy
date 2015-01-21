package org.funivan.intellij.FastCoddy.CodeBuilders.Configuration;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;


public class TemplateItem {
    /**
     * Group used for check insertion position
     * If you do not provide group name shortcut value will be used
     */
    protected String group;

    /**
     * Can be empty
     * MUST be simple and intuitive
     * <p/>
     * example: &
     * example: ucs
     */

    protected String shortcut;

    /**
     * Replace shortcut to this string
     */
    protected String expand;

    /**
     * Used for better template support
     */
    protected String fileRegexp;

    /**
     * Check if shortcut is regex
     * Default value is false
     */
    protected Boolean isRegex = false;

    protected HashMap<String, String> regexpReplaces = new HashMap<String, String>();


    protected HashMap<String, String[]> tabs = new HashMap<String, String[]>();

    protected HashMap<String, VariableConfiguration> vars;


    @Nullable
    public static TemplateItem initFromJson(JSONObject configuration, String filePathPrefix) throws JSONException {
        TemplateItem templateItem = new TemplateItem();


        if (!configuration.has("shortcut")) {
            System.out.println("does not have shortcut");
            return null;
        }

        String shortcut = configuration.getString("shortcut");

        if (shortcut.length() == 0) {
            System.out.println("shortcut has 0 length");
            return null;
        }
        System.out.println("load::");
        System.out.println(shortcut);
        System.out.println("///");
        templateItem.setShortcut(shortcut);

        if (!configuration.has("expand")) {
            System.out.println("does not vae expand");
            return null;
        }


        templateItem.setExpand(configuration.getString("expand"));


        String group = shortcut;
        if (configuration.has("group")) {
            group = configuration.getString("group");
        }
        templateItem.setGroup(group);

        String fileRegexp = filePathPrefix + "/.*";
        if (configuration.has("fileRegexp")) {
            fileRegexp = filePathPrefix + configuration.getString("fileRegexp");
        }

        templateItem.setFileRegexp(fileRegexp);


        if (configuration.has("isRegexp")) {
            templateItem.setIsRegex(configuration.getBoolean("isRegexp"));
        }

        if (configuration.has("regexpReplaces")) {
            templateItem.setIsRegex(true);

            JSONObject regexReplace = configuration.getJSONObject("regexpReplaces");
            Iterator<String> keys = regexReplace.keys();
            while (keys.hasNext()) {
                String from = keys.next();
                String to = regexReplace.getString(from);
                templateItem.addRegexpReplaces(from, to);
            }
        }

        if (configuration.has("tabs")) {

            JSONObject tabs = configuration.getJSONObject("tabs");
            Iterator<String> tabKeys = tabs.keys();
            while (tabKeys.hasNext()) {
                String tabName = tabKeys.next();
                JSONArray tabData = tabs.getJSONArray(tabName);
                String[] tabInclude = new String[tabData.length()];
                for (int i = 0; i < tabData.length(); i++) {
                    tabInclude[i] = tabData.getString(i);
                }

                templateItem.addTab(tabName, tabInclude);
            }
        }


        HashMap<String, VariableConfiguration> vars = new HashMap<String, VariableConfiguration>();

        if (configuration.has("vars")) {
            JSONObject variablesConfig = configuration.getJSONObject("vars");
            Iterator<?> varsNames = variablesConfig.keys();

            while (varsNames.hasNext()) {
                String variableName = (String) varsNames.next();
                JSONObject variable = variablesConfig.getJSONObject(variableName);

                String expression = variable.has("expression") ? variable.getString("expression") : "";
                String defaultValue = variable.has("defaultValue") ? variable.getString("defaultValue") : "";
                Boolean alwaysStopAt = variable.has("alwaysStopAt") ? variable.getBoolean("alwaysStopAt") : false;
                vars.put(variableName, new VariableConfiguration(expression, defaultValue, alwaysStopAt));
            }
        }

        templateItem.setVars(vars);

        return templateItem;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public String getFileRegexp() {
        return fileRegexp;
    }

    public void setFileRegexp(String fileRegexp) {
        this.fileRegexp = fileRegexp;
    }


    public void setIsRegex(Boolean isRegex) {
        this.isRegex = isRegex;
    }

    public boolean isRegexp() {
        return this.isRegex == true;
    }

    public HashMap<String, String> getRegexpReplaces() {
        return regexpReplaces;
    }

    public void addRegexpReplaces(String from, String to) {
        this.regexpReplaces.put(from, to);
    }

    public HashMap<String, String[]> getTabs() {
        return tabs;
    }

    public Boolean hasTabs() {
        return tabs.size() > 0;
    }


    public void addTab(String name, String[] includeTabs) {
        this.tabs.put(name, includeTabs);
    }


    public HashMap<String, VariableConfiguration> getVars() {
        return vars;
    }

    public void setVars(HashMap<String, VariableConfiguration> vars) {
        this.vars = vars;
    }


    public Boolean validForFile(String filePath) {
        if (this.fileRegexp.isEmpty()) {
            return true;
        }
        return (filePath.matches(this.fileRegexp) == true);
    }

}
