/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.devops.conga.tooling.maven.plugin;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import io.wcm.devops.conga.generator.Generator;
import io.wcm.devops.conga.generator.GeneratorOptions;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.tooling.maven.plugin.util.ClassLoaderUtil;
import io.wcm.devops.conga.tooling.maven.plugin.util.MavenContext;

/**
 * Generates configuration using CONGA generator.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresProject = true, threadSafe = true,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateMojo extends AbstractCongaMojo {

  /**
   * Selected environments to generate.
   */
  @Parameter(property = "conga.environments")
  private String[] environments;

  /**
   * Delete folders of environments before generating the new files.
   */
  @Parameter(defaultValue = "false")
  private boolean deleteBeforeGenerate;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Component
  private RepositorySystem repoSystem;
  @Parameter(property = "repositorySystemSession", readonly = true)
  private RepositorySystemSession repoSession;
  @Parameter(property = "project.remotePluginRepositories", readonly = true)
  private List<RemoteRepository> remoteRepos;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    MavenContext mavenContext = new MavenContext()
        .project(project)
        .repoSystem(repoSystem)
        .repoSession(repoSession)
        .remoteRepos(remoteRepos);

    GeneratorOptions options = new GeneratorOptions()
        .baseDir(project.getBasedir())
        .roleDir(getRoleDir())
        .templateDir(getTemplateDir())
        .environmentDir(getEnvironmentDir())
        .destDir(getTargetDir())
        .deleteBeforeGenerate(deleteBeforeGenerate)
        .version(project.getVersion())
        .modelExport(getModelExport())
        .valueProviderConfig(getValueProviderConfig())
        .genericPluginConfig(getPluginConfig())
        .urlFilePluginContainerContext(mavenContext)
        .containerClasspathUrls(ClassLoaderUtil.getMavenProjectClasspathUrls(project))
        .pluginManager(new PluginManagerImpl())
        .dependencyVersionBuilder(new DependencyVersionBuilder(mavenContext))
        .logger(new MavenSlf4jLogFacade(getLog()));

    Generator generator = new Generator(options);
    generator.generate(environments);
  }

}
