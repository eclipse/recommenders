package org.eclipse.recommenders.internal.completion.rcp;

import static java.lang.String.valueOf;
import static org.eclipse.jdt.internal.ui.JavaPluginImages.IMG_OBJS_LOCAL_VARIABLE;
import static org.eclipse.recommenders.utils.Checks.ensureEquals;

import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.text.java.FieldProposalInfo;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.JavaMethodCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.MethodProposalInfo;
import org.eclipse.jdt.internal.ui.text.java.ParameterGuessingProposal;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.google.common.annotations.Beta;

@SuppressWarnings("restriction")
@Beta
public class JavaCompletionProposals {

    public static JavaCompletionProposal newLocalVariableRefProposal(AccessibleCompletionProposal localRef) {
        ensureEquals(localRef.getKind(), CompletionProposal.LOCAL_VARIABLE_REF,
                "proposal kind isn't LOCAL_VARIABLE_REF");
        String replacementString = valueOf(localRef.getCompletion());
        int replacementOffset = localRef.getReplaceStart();
        int replacementLength = localRef.getReplaceEnd() - localRef.getReplaceStart();
        StyledString displayString = new StyledString(valueOf(localRef.getName())).append(
                " : " + valueOf(localRef.getTypeName()), StyledString.QUALIFIER_STYLER);
        Image image = JavaPluginImages.get(IMG_OBJS_LOCAL_VARIABLE);
        int relevance = localRef.getRelevance();
        return new JavaCompletionProposal(replacementString, replacementOffset, replacementLength, image,
                displayString, relevance);
    }

    public static JavaCompletionProposal newFieldRef(AccessibleCompletionProposal fieldRef) {
        ensureEquals(fieldRef.getKind(), CompletionProposal.FIELD_REF, "proposal kind isn't FIELD_REF");
        String replacementString = valueOf(fieldRef.getCompletion());
        int replacementStart = fieldRef.getReplaceStart();
        int replacementLength = fieldRef.getReplaceEnd() - replacementStart;

        String fieldName = valueOf(fieldRef.getName());
        String typeName = valueOf(fieldRef.getTypeName());
        StyledString displayString = new StyledString(fieldName)
        .append(" : " + typeName, StyledString.QUALIFIER_STYLER);
        Image image = JavaElementImageProvider.getFieldImageDescriptor(false, fieldRef.getFlags()).createImage();

        JavaCompletionProposal p = new JavaCompletionProposal(replacementString, replacementStart, replacementLength,
                image, displayString, fieldRef.getRelevance());

        IJavaProject project = fieldRef.getData(IJavaProject.class).orNull();
        if (project != null) {
            p.setProposalInfo(new FieldProposalInfo(project, fieldRef));
        }
        return p;
    }

    public static JavaMethodCompletionProposal newMethodRef(AccessibleCompletionProposal methodRef,
            JavaContentAssistInvocationContext context) {
        CompletionContext coreContext = context.getCoreContext();
        JavaMethodCompletionProposal res;
        if (coreContext != null && coreContext.isExtended()) {
            res = new ParameterGuessingProposal(methodRef, context, coreContext, true);
        } else {
            res = new JavaMethodCompletionProposal(methodRef, context);
        }
        IJavaProject project = methodRef.getData(IJavaProject.class).orNull();
        if (project != null) {
            res.setProposalInfo(new MethodProposalInfo(project, methodRef));
        }
        return res;
    }
}
