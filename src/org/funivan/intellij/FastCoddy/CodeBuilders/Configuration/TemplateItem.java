package org.funivan.intellij.FastCoddy.CodeBuilders.Configuration;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;


/**
 * Template item defined in the configuration file
 */
public class TemplateItem {
    /**
     * Group used for check insertion position
     * If you do not provide group name shortcut value will be used
     */
    private String group;

    /**
     * Can be empty
     * MUST be simple and intuitive
     * <p/>
     * example: &
     * example: ucs
     */

    private String shortcut;

    /**
     * Replace shortcut to this string
     */
    private String expand;

    /**
     * Used for better template support
     */
    private String fileRegexp;

    /**
     * Check if shortcut is regex
     * Default value is false
     */
    private Boolean isRegex = false;


    private HashMap<String, String> regexpReplaces = new HashMap<>();


    private HashMap<String, String[]> tabs = new HashMap<>();

    private LinkedHashMap<String, VariableConfiguration> vars;


    @Nullable
    public static TemplateItem initFromJson(JSONObject configuration, String filePathPrefix) throws JSONException {
        TemplateItem templateItem = new TemplateItem();


        if (!configuration.has("shortcut")) {
            return null;
        }

        String shortcut = configuration.getString("shortcut");

        if (shortcut.length() == 0) {
            return null;
        }

        templateItem.setShortcut(shortcut);

        if (!configuration.has("expand")) {
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


        LinkedHashMap<String, VariableConfiguration> vars = new LinkedHashMap<>();

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

    private void setGroup(String group) {
        this.group = group;
    }

    public String getExpand() {
        return expand;
    }

    private void setExpand(String expand) {
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


    public LinkedHashMap<String, VariableConfiguration> getVars() {
        return vars;
    }

    public void setVars(LinkedHashMap<String, VariableConfiguration> vars) {
        this.vars = vars;
    }


}
