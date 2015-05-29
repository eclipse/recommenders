package org.eclipse.recommenders.internal.types.rcp;

import static com.google.common.base.Optional.of;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class TypesIndexServiceTest {

    private static final Optional<IProjectTypesIndex> NO_INDEX = Optional.<IProjectTypesIndex>absent();

    private static final ITypeName JAVA_UTIL_LIST = VmTypeName.get("Ljava/util/List");

    private static final int ELEMENT_DELTA_KIND_UNSET = 0;
    private static final int ELEMENT_DELTA_FLAGS_UNSET = 0;

    @Test
    public void testSubtypesOfUnknownProject() {
        IIndexProvider provider = mock(IIndexProvider.class);
        IJavaProject project = mock(IJavaProject.class);
        Mockito.when(provider.findOrCreateIndex(project)).thenReturn(NO_INDEX);

        TypesIndexService sut = new TypesIndexService(provider);
        Set<String> subtypes = sut.subtypes(JAVA_UTIL_LIST, "a", project);

        assertThat(subtypes.isEmpty(), is(true));
    }

    @Test
    public void testSubtypesOfKnownProject() {
        IIndexProvider provider = mock(IIndexProvider.class);
        IJavaProject project = mock(IJavaProject.class);
        IProjectTypesIndex index = mock(IProjectTypesIndex.class);
        when(provider.findOrCreateIndex(project)).thenReturn(of(index));

        TypesIndexService sut = new TypesIndexService(provider);
        sut.subtypes(JAVA_UTIL_LIST, "a", project);

        verify(index).subtypes(JAVA_UTIL_LIST, "a");
    }

    @Test
    public void testEventForNewProject() {
        IJavaProject newJavaProject = mockProject();
        IJavaElement element = mockElement(newJavaProject);
        IJavaElementDelta newProjectDelta = mockElementDelta(newJavaProject, IJavaElementDelta.ADDED,
                ELEMENT_DELTA_FLAGS_UNSET);

        IJavaElementDelta javaModelDelta = mockElementDelta(element, ELEMENT_DELTA_KIND_UNSET,
                IJavaElementDelta.F_CHILDREN, newProjectDelta);
        ElementChangedEvent projectAddedEvent = mockElementChangedEvent(javaModelDelta);

        IIndexProvider provider = mock(IIndexProvider.class);
        IProjectTypesIndex index = mockProspectiveIndex(newJavaProject, provider);

        TypesIndexService sut = new TypesIndexService(provider);
        sut.elementChanged(projectAddedEvent);

        verifyZeroInteractions(index);
    }

    @Test
    public void testEventForClasspathChangeOfUnknownProject() {
        IJavaProject newJavaProject = mockProject();
        IJavaElementDelta changedProjectDelta = mockElementDelta(newJavaProject, IJavaElementDelta.CHANGED,
                IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED);

        ElementChangedEvent projectChangedEvent = mockElementChangedEvent(changedProjectDelta);
        IIndexProvider provider = mock(IIndexProvider.class);

        IProjectTypesIndex index = mockProspectiveIndex(newJavaProject, provider);

        TypesIndexService sut = new TypesIndexService(provider);
        sut.elementChanged(projectChangedEvent);

        verifyZeroInteractions(index);
    }

    @Test
    public void testEventForClasspathChangeOfKnownProject() {
        IJavaProject newJavaProject = mockProject();
        IJavaElementDelta changedProjectDelta = mockElementDelta(newJavaProject, IJavaElementDelta.CHANGED,
                IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED);

        ElementChangedEvent projectChangedEvent = mockElementChangedEvent(changedProjectDelta);
        IIndexProvider provider = mock(IIndexProvider.class);

        IProjectTypesIndex index = mockExistingIndex(newJavaProject, provider);

        TypesIndexService sut = new TypesIndexService(provider);
        sut.elementChanged(projectChangedEvent);

        verify(index).suggestRebuild();
    }

    public IProjectTypesIndex mockProspectiveIndex(IJavaProject newJavaProject, IIndexProvider provider) {
        IProjectTypesIndex index = mock(IProjectTypesIndex.class);
        when(provider.findIndex(newJavaProject)).thenReturn(NO_INDEX);
        when(provider.findOrCreateIndex(newJavaProject)).thenReturn(of(index));
        return index;
    }

    public IProjectTypesIndex mockExistingIndex(IJavaProject newJavaProject, IIndexProvider provider) {
        IProjectTypesIndex index = mock(IProjectTypesIndex.class);
        when(provider.findIndex(newJavaProject)).thenReturn(of(index));
        when(provider.findOrCreateIndex(newJavaProject)).thenReturn(of(index));
        return index;
    }

    private IJavaProject mockProject() {
        IJavaProject project = mock(IJavaProject.class);
        when(project.getJavaProject()).thenReturn(project);
        return project;
    }

    private IJavaElement mockElement(IJavaProject newJavaProject) {
        IJavaElement element = mock(IJavaElement.class);
        when(element.getJavaProject()).thenReturn(newJavaProject);
        return element;
    }

    private ElementChangedEvent mockElementChangedEvent(IJavaElementDelta javaElementDelta) {
        ElementChangedEvent event = mock(ElementChangedEvent.class);
        when(event.getDelta()).thenReturn(javaElementDelta);
        return event;
    }

    private IJavaElementDelta mockElementDelta(IJavaElement element, int kind, int flags,
            IJavaElementDelta... affectedChildren) {
        IJavaElementDelta delta = mock(IJavaElementDelta.class);

        when(delta.getFlags()).thenReturn(flags);
        when(delta.getElement()).thenReturn(element);
        when(delta.getKind()).thenReturn(kind);
        when(delta.getAffectedChildren()).thenReturn(affectedChildren);

        return delta;
    }

}
