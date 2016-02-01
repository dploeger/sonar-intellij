package org.sonarlint.intellij;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.impl.MessageBusImpl;
import org.junit.After;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class SonarTest {
  protected Project project;
  protected Module module;
  protected VirtualFile root;
  protected Application app;

  @Before
  public void setUp() {
    project = createProject();
    module = createModule();
    app = mock(Application.class);
    ApplicationManager.setApplication(app, mock(Disposable.class));
    createModuleRoot();
  }

  @After
  public void tearDown() {
    project = null;
    module = null;
  }

  private Project createProject() {
    Project project = mock(Project.class);
    when(project.getMessageBus()).thenReturn(new MessageBusImpl.RootBus(this));
    when(project.isDisposed()).thenReturn(false);

    return project;
  }

  private void createModuleRoot() {
    ModuleRootManager moduleRootManager = mock(ModuleRootManager.class);
    root = mock(VirtualFile.class);
    when(root.getCanonicalPath()).thenReturn("/src");
    when(root.getPath()).thenReturn("/src");
    VirtualFile[] roots = {root};
    when(moduleRootManager.getContentRoots()).thenReturn(roots);
    register(module, ModuleRootManager.class, moduleRootManager);
  }

  protected Module createModule() {
    Module m = mock(Module.class);
    when(m.getName()).thenReturn("testModule");
    when(m.getProject()).thenReturn(project);
    return m;
  }

  protected Project getProject() {
    return project;
  }

  protected void register(Class<?> clazz, Object instance) {
    register(project, clazz, instance);
  }

  protected void register(ComponentManager comp, Class<?> clazz, Object instance) {
    when(comp.getComponent(clazz)).thenReturn(instance);
  }
}
