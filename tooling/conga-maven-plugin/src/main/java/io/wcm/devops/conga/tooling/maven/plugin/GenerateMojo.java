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

import io.wcm.devops.conga.generator.Generator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Generates configuration using CONGA generator.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresProject = true, threadSafe = true)
public class GenerateMojo extends AbstractCongaMojo {

  /**
   * Selected environments to generate.
   */
  @Parameter
  private String[] environments;

  /**
   * Delete folders of environments before generating the new files.
   */
  @Parameter(defaultValue = "false")
  private boolean deleteBeforeGenerate;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Generator generator = new Generator(getRoleDir(), getEnvironmentDir(), getTemplateDir(), getTargetDir());
    generator.setLogger(new MavenSlf4jLogFacade(getLog()));
    generator.setDeleteBeforeGenerate(deleteBeforeGenerate);
    generator.generate(environments);
  }

}
