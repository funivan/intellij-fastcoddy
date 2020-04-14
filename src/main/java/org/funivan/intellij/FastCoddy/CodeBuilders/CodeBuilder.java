package org.funivan.intellij.FastCoddy.CodeBuilders;


import com.intellij.psi.PsiFile;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.TemplateItem;
import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.VariableConfiguration;
import org.funivan.intellij.FastCoddy.FastCoddyAppComponent;
import org.funivan.intellij.FastCoddy.Helper.FileHelper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
public class CodeBuilder implements CodeBuilderInterface {


    private ArrayList<TemplateItem> templateItems;
    private ArrayList<String> delimiters;

    public CodeBuilder(String filePath) throws JSONException {
        this.templateItems = new ArrayList<>();
        this.delimiters = new ArrayList<>();
        this.loadConfigFromFile(filePath);

    }


    /**
     * Return new code string
     */
    public CodeTemplate expandCodeFromShortcut(String shortcut, PsiFile psiFile) {


        CodeTemplate newCodeTemplate = null;

        try {

            String filePath = psiFile.getVirtualFile().getPath();
            List<LocalShortcutItem> list = this.getShortcutItems(shortcut, filePath);

            newCodeTemplate = this.getNewCode(list);
            newCodeTemplate.setInitialString(shortcut);
            newCodeTemplate.setUsedShortCodesNum(list.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newCodeTemplate;
    }

    private void loadConfigFromFile(String filePath) throws JSONException {
        String jsonString = FileHelper.getFileContent(filePath);

        if (jsonString == null || jsonString.isEmpty()) {
            return;
        }

        JSONObject obj = new JSONObject(jsonString);

        if (!obj.has("items")) {
            throw new JSONException("Empty items section inside the file: " + filePath);
        }

        if (!obj.has("delimiters")) {
            throw new JSONException("Empty delimiters section inside the file: " + filePath);
        }

        JSONArray delimiters = obj.getJSONArray("delimiters");

        for (int delimiterIndex = delimiters.length() - 1; delimiterIndex >= 0; delimiterIndex--) {
            String itemDelimiter = delimiters.getString(delimiterIndex);
            this.delimiters.add(itemDelimiter);
        }


        JSONArray itemTemplates = obj.getJSONArray("items");


        for (int i = itemTemplates.length() - 1; i >= 0; i--) {
            JSONObject itemConfiguration = itemTemplates.getJSONObject(i);
            TemplateItem templateItem = TemplateItem.initFromJson(itemConfiguration);

            if (templateItem != null) {
                this.templateItems.add(templateItem);
            }
        }


    }

    /**
     * <p/>
     * we have string if!e
     * iterate
     * if is top than i in our config so take if and cut is
     * <p/>
     * we have string !e
     * iterate over config and match. ! is matched so cut it
     * <p/>
     * we have string e
     * <p/>
     */
    private CodeTemplate getNewCode(List<LocalShortcutItem> localShortcutItemList) throws JSONException {

        String newCode = "";
        LinkedHashMap<String, VariableConfiguration> variablesConfiguration = new LinkedHashMap<>();


        List<String> insertedTabs = new ArrayList<>();
        for (int index = 0; index < localShortcutItemList.size(); index++) {

            LocalShortcutItem localShortcutItem = localShortcutItemList.get(index);

            Integer shortcutKey = localShortcutItem.getKey();
            String shortcutTpl = localShortcutItem.getCode();
            TemplateItem item = this.templateItems.get(shortcutKey);

            // merge variables
            LinkedHashMap<String, VariableConfiguration> variables = localShortcutItem.getTemplateItem().getVars();
            for (String key : variables.keySet()) {
                variablesConfiguration.put(key, variables.get(key));
            }


            // prepare new template
            shortcutTpl = shortcutTpl.replaceAll("\\$TAB([0-9]+)\\$", "\\$TAB_" + index + "_$1\\$");

            if (newCode.isEmpty() || index == 0) {
                newCode = shortcutTpl;
            } else {
                Integer prevIndex = index;
                String insertPosition = getInsertPosition(localShortcutItemList, item, prevIndex);
                newCode = newCode.replace(insertPosition, shortcutTpl + insertPosition);
                insertedTabs.add(insertPosition);
            }


            // refresh end position
            newCode = newCode.replaceAll("\\$END\\$", "");
            newCode += "$END$";
        }

        newCode = newCode.replaceAll("\\$LAST\\$", "");
        newCode = newCode.replaceAll("\\$END\\$", "");
        newCode = newCode.replaceAll("(\\$TAB_[0-9]+_[0-9]+\\$)+", "$1");


        // remove previous inserted positions
        for (String tabId : insertedTabs) {
            newCode = newCode.replace(tabId, "");
        }

        return new CodeTemplate(newCode, variablesConfiguration);

    }

    private String getInsertPosition(List<LocalShortcutItem> shortCodeConfiguration, TemplateItem item, Integer prevIndex) throws JSONException {
        Integer iTmp = 0;
        String insertPosition = "";
        Integer leftPreviousIndexes = prevIndex;
        do {

            leftPreviousIndexes--;
            if (leftPreviousIndexes < 0) {
                break;
            }

            LocalShortcutItem previousLocalShortcutItem = shortCodeConfiguration.get(leftPreviousIndexes);

            TemplateItem prevItem = this.templateItems.get(previousLocalShortcutItem.getKey());
            if (prevItem != null) {
//                System.out.println("Prev item:" + prevItem.getShortcut());

                if (prevItem.hasTabs()) {
                    // detect in what place we need to insert our code
                    HashMap<String, String[]> tabs = prevItem.getTabs();

                    Iterator keys = tabs.keySet().iterator();

                    Boolean detectPosition = false;


                    if (!insertPosition.equals("")) {
                        break;
                    }

                    while (keys.hasNext() && !detectPosition) {
                        String tabIndex = (String) keys.next();
                        String[] currentTabGroups = tabs.get(tabIndex);
                        String itemGroup = item.getGroup();

                        Integer tabGroupsLen = currentTabGroups.length;
                        if (tabGroupsLen == 0) {
                            insertPosition = "$" + tabIndex.replace("TAB", "TAB_" + (leftPreviousIndexes) + '_') + "$";
                        } else {
                            for (String placeToGroup : currentTabGroups) {
                                if (placeToGroup.equals(itemGroup)) {
                                    insertPosition = "$" + tabIndex.replace("TAB", "TAB_" + (leftPreviousIndexes) + '_') + "$";
                                    detectPosition = true;
                                    break;
                                }
                            }
                        }

                    }

                }
            }
            iTmp++;
        } while (prevIndex > 0 && insertPosition.isEmpty() && iTmp < 50);
        // default position is end

        if (insertPosition.isEmpty()) {
            insertPosition = "$END$";
        }
        return insertPosition;
    }

    /**
     * We have following string `!e&isf` each word represented as LocalShortcutItem
     * ! - LocalShortcutItem    (not)
     * e - LocalShortcutItem    empty
     * & - LocalShortcutItem    and
     * isf - LocalShortcutItem  is_file
     * <p/>
     * In this method we detect pars from string according to our configuration
     *
     * @throws JSONException
     */
    private List<LocalShortcutItem> getShortcutItems(String typedString, String filePath) throws JSONException {
        List<LocalShortcutItem> shortcutsExpand = new ArrayList<>();

        typedString = typedString.trim();

        if (typedString.length() == 0) {
            return shortcutsExpand;
        }

        while (typedString.length() > 0) {
            Boolean added = false;

            for (int index = this.templateItems.size() - 1; index >= 0; index--) {
                TemplateItem templateItem = this.templateItems.get(index);


                if (!filePath.matches(templateItem.getFileRegex())) {
                    continue;
                }

                typedString = typedString.trim();

                String shortcut = templateItem.getShortcut();


                if (templateItem.isRegex()) {

                    String regex = "^" + shortcut + "(.*)$";
                    Pattern pattern = null;
                    try {
                        pattern = Pattern.compile(regex);
                    } catch (PatternSyntaxException ex) {
                        FastCoddyAppComponent.LOG.error("Invalid pattern:" + shortcut, "Error:" + ex.getMessage());
                    }

                    if (pattern == null) {
                        continue;
                    }

                    Matcher matcher = pattern.matcher(typedString);

                    if (matcher.find()) {
                        String expandToString = templateItem.getExpand();


                        HashMap<String, String> regexReplace = templateItem.getRegexReplaces();

                        int groupsNum = matcher.groupCount();

                        for (int groupIndex = 1; groupIndex <= groupsNum; groupIndex++) {
                            String groupString = matcher.group(groupIndex);


                            groupString = (groupString != null) ? groupString : "";


                            if (groupIndex == groupsNum) {
                                typedString = groupString;
                            } else {

                                String newGroupString = regexReplace.get(groupString);
                                if (newGroupString != null) {
                                    groupString = newGroupString;
                                }
                                expandToString = expandToString.replaceAll("\\$" + groupIndex, groupString);
                            }
                        }

                        LocalShortcutItem localShortcutItem = new LocalShortcutItem(index, expandToString, templateItem);
                        shortcutsExpand.add(localShortcutItem);
                        added = true;
                        break;
                    }


                } else if (typedString.indexOf(shortcut) == 0) {
                    LocalShortcutItem localShortcutItem = new LocalShortcutItem(index, templateItem.getExpand(), templateItem);
                    shortcutsExpand.add(localShortcutItem);
                    typedString = typedString.substring(shortcut.length());

                    added = true;
                    break;
                }
            }

            if (!added) {
                shortcutsExpand.clear();
                return shortcutsExpand;
            }
        }

        return shortcutsExpand;
    }

    public ArrayList<String> getDelimiters() {
        return delimiters;
    }
}
