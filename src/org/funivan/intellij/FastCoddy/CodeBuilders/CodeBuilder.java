package org.funivan.intellij.FastCoddy.CodeBuilders;


import com.intellij.psi.PsiFile;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.TemplateItem;
import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.VariableConfiguration;
import org.funivan.intellij.FastCoddy.Helper.FileHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: funivan
 * Date: 12/23/13
 */
public class CodeBuilder implements CodeBuilderInterface {

    private JSONObject options;
    private ArrayList<TemplateItem> templateItems;

    public CodeBuilder() throws JSONException {
        this.options = new JSONObject();
        this.templateItems = new ArrayList<TemplateItem>();
    }


    /**
     * Return new code string
     */
    public CodeTemplate expandCodeFromShortcut(String shortcut, PsiFile psiFile) {


        List<LocalShortcutItem> list = null;
        CodeTemplate newCodeTemplate = null;

        try {

            String filePath = psiFile.getVirtualFile().getPath();

            list = this.getShortcutItems(shortcut, filePath);
            newCodeTemplate = this.getNewCode(list);
            newCodeTemplate.setInitialString(shortcut);
            newCodeTemplate.setUsedShortCodesNum(list.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newCodeTemplate;
    }

    public void loadConfigFromFile(String filePath, String filePathPrefix) {
        String jsonString = FileHelper.getFileContent(filePath);

        if (jsonString == null || jsonString.isEmpty()) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(jsonString);

            if (!obj.has("items")) {
                return;
            }

            JSONArray itemTemplates = obj.getJSONArray("items");

            Integer maxIndex = this.templateItems.size();
            System.out.println("maxIndex:" + maxIndex);
            // prepare config
            // load configuration from end to start
            Integer index = 0;
            for (int i = itemTemplates.length() - 1; i >= 0; i--) {
                JSONObject itemConfiguration = itemTemplates.getJSONObject(i);
                TemplateItem templateItem = TemplateItem.initFromJson(itemConfiguration, filePathPrefix);

                if (templateItem != null) {
                    this.templateItems.add(templateItem);
                    index++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
    protected CodeTemplate getNewCode(List<LocalShortcutItem> localShortcutItemList) throws JSONException {

        String newCode = "";
        HashMap<String, VariableConfiguration> variablesConfiguration = new HashMap<String, VariableConfiguration>();


        List<String> insertedTabs = new ArrayList<String>();
        System.out.println("                       ==================                          ");
        for (int index = 0; index < localShortcutItemList.size(); index++) {

            LocalShortcutItem localShortcutItem = localShortcutItemList.get(index);

            Integer shortcutKey = localShortcutItem.getKey();
            String shortcutTpl = localShortcutItem.getCode();
            TemplateItem item = this.templateItems.get(shortcutKey);

            // merge variables
            HashMap<String, VariableConfiguration> variables = localShortcutItem.getTemplateItem().getVars();
            for (String key : variables.keySet()) {
                variablesConfiguration.put(key, variables.get(key));
            }


            // prepare new template
            shortcutTpl = shortcutTpl.replaceAll("\\$TAB([0-9]+)\\$", "\\$TAB_" + index + "_$1\\$");

            System.out.println("shortcutTpl::" + shortcutTpl);
            if (newCode.isEmpty() || index == 0) {
                newCode = shortcutTpl;
            } else {
                // detect where we need to add this item
                Integer prevIndex = index;
                System.out.println("prevIndex:" + prevIndex);
                String insertPosition = getInsertPosition(localShortcutItemList, item, prevIndex);
                System.out.println("insertPosition::" + insertPosition);
                System.out.println("oldCode:" + newCode);
                newCode = newCode.replace(insertPosition, shortcutTpl + insertPosition);
                insertedTabs.add(insertPosition);

                System.out.println("newCode:" + newCode);
                System.out.println("...\n");
            }


            // refresh end position
            newCode = newCode.replaceAll("\\$END\\$", "");
            newCode += "$END$";
        }

        newCode = newCode.replaceAll("\\$LAST\\$", "");
        newCode = newCode.replaceAll("\\$END\\$", "");
        newCode = newCode.replaceAll("(\\$TAB_[0-9]+_[0-9]+\\$)+", "$1");


        // remove previous inserted positions
        for (int insertedIndexTab = 0; insertedIndexTab < insertedTabs.size(); insertedIndexTab++) {
            String tabId = insertedTabs.get(insertedIndexTab);
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
                System.out.println("Prev item:" + prevItem.getShortcut());

                if (prevItem.hasTabs()) {
                    // detect in what place we need to insert our code
                    HashMap<String, String[]> tabs = prevItem.getTabs();

                    Iterator keys = tabs.keySet().iterator();

                    Boolean detectPosition = false;
                    
                    
                    if(insertPosition.equals("")==false){
                        detectPosition = true;
                        break;
                    }
                    
                    while (keys.hasNext() && detectPosition == false) {
                        String tabIndex = (String) keys.next();
                        String[] currentTabGroups = tabs.get(tabIndex);
                        String itemGroup = item.getGroup();
                        System.out.println("itemGroup::" + itemGroup);

                        Integer tabGroupsLen = currentTabGroups.length;
                        if (tabGroupsLen == 0) {
                            // use tabs: { TAB2: []} ] to place all items to tab2
                            System.out.println("leftPreviousIndexes:" + leftPreviousIndexes);
                            System.out.println("prevIndex:" + prevIndex);
                            Integer testIndex = leftPreviousIndexes + prevIndex - 1;
                            System.out.println("testIndex :" + testIndex);
                            insertPosition = "$" + tabIndex.replace("TAB", "TAB_" + (leftPreviousIndexes) + '_') + "$";                           
                        } else {
                            for (int i = 0; i < currentTabGroups.length; i++) {
                                String placeToGroup = currentTabGroups[i];
                                System.out.println("placeToGroup::" + placeToGroup);
                                if (placeToGroup.equals(itemGroup)) {
                                    System.out.println("Group are equal");

                                    System.out.println("leftPreviousIndexes:" + leftPreviousIndexes);
                                    System.out.println("prevIndex:" + prevIndex);
                                    Integer testIndex = leftPreviousIndexes + prevIndex - 1;
                                    System.out.println("testIndex :" + testIndex);
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
     * @param typesString
     * @param filePath
     * @return
     * @throws JSONException
     */
    protected List<LocalShortcutItem> getShortcutItems(String typesString, String filePath) throws JSONException {
        List<LocalShortcutItem> shortcutsExpand = new ArrayList<LocalShortcutItem>();

        typesString = typesString.trim();

        if (typesString.length() == 0) {
            return shortcutsExpand;
        }

        while (typesString.length() > 0) {
            Boolean added = false;

            for (int index = this.templateItems.size() - 1; index >= 0; index--) {
                TemplateItem templateItem = this.templateItems.get(index);

                if (!templateItem.validForFile(filePath)) {
                    continue;
                }

                typesString = typesString.trim();

                String shortcut = templateItem.getShortcut();


                if (templateItem.isRegexp()) {

                    String regex = "^" + shortcut + "(.*)$";
                    System.out.println("regex:" + regex);

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(typesString);

                    if (matcher.find()) {
                        String expandToString = templateItem.getExpand();


                        HashMap<String, String> regexReplace = templateItem.getRegexpReplaces();

                        int groupsNum = matcher.groupCount();

                        for (int groupIndex = 1; groupIndex <= groupsNum; groupIndex++) {
                            String groupString = matcher.group(groupIndex);


                            groupString = (groupString != null) ? groupString : "";


                            if (groupIndex == groupsNum) {
                                typesString = groupString;
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
                        System.out.println("new string:" + expandToString);

                        added = true;
                        break;
                    }


                } else if (typesString.indexOf(shortcut) == 0) {
                    LocalShortcutItem localShortcutItem = new LocalShortcutItem(index, templateItem.getExpand(), templateItem);
                    shortcutsExpand.add(localShortcutItem);
                    typesString = typesString.substring(shortcut.length());

                    added = true;
                    break;
                }
            }

            if (added == false) {
                shortcutsExpand.clear();
                return shortcutsExpand;
            }
        }

        return shortcutsExpand;
    }


}
