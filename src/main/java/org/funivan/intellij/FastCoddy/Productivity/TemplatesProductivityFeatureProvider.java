package org.funivan.intellij.FastCoddy.Productivity;

import com.intellij.featureStatistics.ApplicabilityFilter;
import com.intellij.featureStatistics.FeatureDescriptor;
import com.intellij.featureStatistics.GroupDescriptor;
import com.intellij.featureStatistics.ProductivityFeaturesProvider;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
public class TemplatesProductivityFeatureProvider extends ProductivityFeaturesProvider implements com.intellij.openapi.components.ApplicationComponent {

    private static final String GROUP_DESCRIPTOR_ID = "fast-coddy.feature";

    static final String LIVE_TEMPLATE_INVOKE = "fast-coddy.template.invoke";


    private FeatureDescriptor[] descriptors;

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "TemplatesProductivityFeatureProvider";
    }

    public void initComponent() {

        List<FeatureDescriptor> list = new ArrayList<>();
        list.add(createFeatureDescriptor());


        descriptors = list.toArray(new FeatureDescriptor[list.size()]);
    }


    @NotNull
    private FeatureDescriptor createFeatureDescriptor() {
        return new FeatureDescriptor(TemplatesProductivityFeatureProvider.LIVE_TEMPLATE_INVOKE, GROUP_DESCRIPTOR_ID, "TemplateInvoke.html",
                "Expand template",
                0, 1, null, 1, this);
    }

    public FeatureDescriptor[] getFeatureDescriptors() {
        return descriptors;
    }

    public GroupDescriptor[] getGroupDescriptors() {
        return new GroupDescriptor[]{new GroupDescriptor(GROUP_DESCRIPTOR_ID, "FastCoddy")};
    }

    public ApplicabilityFilter[] getApplicabilityFilters() {
        return new ApplicabilityFilter[]{
                new ApplicabilityFilter() {
                    public String getPrefix() {
                        return GROUP_DESCRIPTOR_ID;
                    }

                    public boolean isApplicable(String string, Project project) {
                        return true;
                    }
                }
        };
    }


}

