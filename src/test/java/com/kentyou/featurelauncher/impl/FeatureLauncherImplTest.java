/**
 * Copyright (c) 2024 Kentyou and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Kentyou - initial implementation
 */
package com.kentyou.featurelauncher.impl;

import static com.kentyou.featurelauncher.impl.repository.ArtifactRepositoryConstants.LOCAL_ARTIFACT_REPOSITORY_PATH;
import static com.kentyou.featurelauncher.impl.repository.ArtifactRepositoryConstants.REMOTE_ARTIFACT_REPOSITORY_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.osgi.service.featurelauncher.repository.ArtifactRepositoryConstants.ARTIFACT_REPOSITORY_NAME;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.service.featurelauncher.FeatureLauncher;
import org.osgi.service.featurelauncher.repository.ArtifactRepository;

import com.kentyou.featurelauncher.impl.util.BundleStateUtil;
import com.kentyou.featurelauncher.impl.util.ServiceLoaderUtil;

/**
 * Tests {@link com.kentyou.featurelauncher.impl.FeatureLauncherImpl}
 * 
 * As defined in: "160.4 The Feature Launcher"
 * 
 * @author Michael H. Siemaszko (mhs@into.software)
 * @since Sep 17, 2024
 */
public class FeatureLauncherImplTest {
	FeatureLauncher featureLauncher;
	Path localM2RepositoryPath;
	Map<String, String> frameworkProperties;

	@TempDir
	Path frameworkStorageTempDir;

	@BeforeEach
	public void setUp(TestInfo info) throws InterruptedException, IOException {
		// Obtain path of dedicated local Maven repository
		localM2RepositoryPath = Paths.get(System.getProperty(LOCAL_ARTIFACT_REPOSITORY_PATH, "target/m2Repo"));
		assertTrue(Files.exists(localM2RepositoryPath), "No local artifact repository available at "
				+ localM2RepositoryPath + " missing system property or maven setup.");

		// Configure framework properties
		System.out.println(
				"*** Using " + frameworkStorageTempDir + " for framework storage in test " + info.getDisplayName());
		frameworkProperties = Map.of(Constants.FRAMEWORK_STORAGE, frameworkStorageTempDir.toString());

		// Load the Feature Launcher
		featureLauncher = ServiceLoaderUtil.loadFeatureLauncherService();
	}

	// The use of Gogo Shell requires that Std In is connected to a live
	// terminal, which breaks builds using batch mode (like CI).
	// We therefore tell gogo to be non-interactive

	@BeforeEach
	public void replaceStdInForGogo() throws IOException {
		System.setProperty("gosh.args", "-s");
	}

	@AfterEach
	public void resetStdIn() {
		System.clearProperty("gosh.args");
	}

	@Test
	public void testLaunchFeatureWithNoConfigWithDefaultFramework()
			throws IOException, InterruptedException, URISyntaxException, BundleException {
		// Set up a repositories
		ArtifactRepository localArtifactRepository = featureLauncher.createRepository(localM2RepositoryPath);
		assertNotNull(localArtifactRepository);

		ArtifactRepository remoteRepository = featureLauncher.createRepository(REMOTE_ARTIFACT_REPOSITORY_URI,
				Map.of(ARTIFACT_REPOSITORY_NAME, "central", LOCAL_ARTIFACT_REPOSITORY_PATH,
						localM2RepositoryPath.toString()));
		assertNotNull(remoteRepository);

		// Read Feature JSON
		Path featureJSONPath = Paths.get(getClass().getResource("/features/gogo-console-feature.json").toURI());

		// Launch the framework
		// @formatter:off
		Framework osgiFramework = featureLauncher.launch(Files.newBufferedReader(featureJSONPath))
				.withRepository(localArtifactRepository)
				.withRepository(remoteRepository)
				.withFrameworkProperties(frameworkProperties)
				.launchFramework();
		// @formatter:on

		// Verify bundles defined in feature are installed and started
		Bundle[] bundles = osgiFramework.getBundleContext().getBundles();
		assertEquals(4, bundles.length);

		assertEquals("org.apache.felix.gogo.command", bundles[1].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[1].getState()));

		assertEquals("org.apache.felix.gogo.shell", bundles[2].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[2].getState()));

		assertEquals("org.apache.felix.gogo.runtime", bundles[3].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[3].getState()));

		// Stop framework
		osgiFramework.stop();
		osgiFramework.waitForStop(0);
	}

	@Test
	public void testLaunchFeatureWithConfigWithDefaultFramework()
			throws IOException, InterruptedException, URISyntaxException, BundleException {
		// Set up a repositories
		ArtifactRepository localArtifactRepository = featureLauncher.createRepository(localM2RepositoryPath);
		assertNotNull(localArtifactRepository);

		ArtifactRepository remoteRepository = featureLauncher.createRepository(REMOTE_ARTIFACT_REPOSITORY_URI,
				Map.of(ARTIFACT_REPOSITORY_NAME, "central", LOCAL_ARTIFACT_REPOSITORY_PATH,
						localM2RepositoryPath.toString()));
		assertNotNull(remoteRepository);

		// Read Feature JSON
		Path featureJSONPath = Paths.get(getClass().getResource("/features/console-webconsole-feature.json").toURI());

		// Launch the framework
		// @formatter:off
		Framework osgiFramework = featureLauncher.launch(Files.newBufferedReader(featureJSONPath))
				.withRepository(localArtifactRepository)
				.withRepository(remoteRepository)
				.withFrameworkProperties(frameworkProperties)
				.launchFramework();
		// @formatter:on

		// Verify bundles defined in feature are installed and started
		Bundle[] bundles = osgiFramework.getBundleContext().getBundles();
		assertEquals(15, bundles.length);

		assertEquals("org.apache.felix.configadmin", bundles[1].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[1].getState()));

		assertEquals("org.apache.felix.gogo.command", bundles[2].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[2].getState()));

		assertEquals("org.apache.felix.gogo.shell", bundles[3].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[3].getState()));

		assertEquals("org.apache.felix.gogo.runtime", bundles[4].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[4].getState()));

		assertEquals("biz.aQute.gogo.commands.provider", bundles[5].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[5].getState()));

		assertEquals("org.apache.felix.webconsole", bundles[14].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[14].getState()));

		// Stop framework
		osgiFramework.stop();
		osgiFramework.waitForStop(0);
	}

	@Test
	public void testLaunchFeatureWithLaunchFrameworkExtension()
			throws IOException, InterruptedException, URISyntaxException, BundleException {
		// Set up a repositories
		ArtifactRepository localArtifactRepository = featureLauncher.createRepository(localM2RepositoryPath);
		assertNotNull(localArtifactRepository);

		ArtifactRepository remoteRepository = featureLauncher.createRepository(REMOTE_ARTIFACT_REPOSITORY_URI,
				Map.of(ARTIFACT_REPOSITORY_NAME, "central", LOCAL_ARTIFACT_REPOSITORY_PATH,
						localM2RepositoryPath.toString()));
		assertNotNull(remoteRepository);

		// Read Feature JSON
		Path featureJSONPath = Paths
				.get(getClass().getResource("/features/gogo-console-launch-framework-extension-feature.json").toURI());

		// Launch the framework
		// @formatter:off
		Framework osgiFramework = featureLauncher.launch(Files.newBufferedReader(featureJSONPath))
				.withRepository(localArtifactRepository)
				.withRepository(remoteRepository)
				.withFrameworkProperties(frameworkProperties)
				.launchFramework();
		// @formatter:on

		// Verify bundles defined in feature are installed and started
		Bundle[] bundles = osgiFramework.getBundleContext().getBundles();
		assertEquals(4, bundles.length);

		assertEquals("org.apache.felix.gogo.command", bundles[1].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[1].getState()));

		assertEquals("org.apache.felix.gogo.shell", bundles[2].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[2].getState()));

		assertEquals("org.apache.felix.gogo.runtime", bundles[3].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[3].getState()));

		// Stop framework
		osgiFramework.stop();
		osgiFramework.waitForStop(0);
	}

	@Test
	public void testLaunchFeatureWithNonMandatoryLaunchFrameworkExtension()
			throws IOException, InterruptedException, URISyntaxException, BundleException {
		// Set up a repositories
		ArtifactRepository localArtifactRepository = featureLauncher.createRepository(localM2RepositoryPath);
		assertNotNull(localArtifactRepository);

		ArtifactRepository remoteRepository = featureLauncher.createRepository(REMOTE_ARTIFACT_REPOSITORY_URI,
				Map.of(ARTIFACT_REPOSITORY_NAME, "central", LOCAL_ARTIFACT_REPOSITORY_PATH,
						localM2RepositoryPath.toString()));
		assertNotNull(remoteRepository);

		// Read Feature JSON
		Path featureJSONPath = Paths.get(getClass()
				.getResource("/features/gogo-console-launch-framework-extension-feature.non-mandatory.json").toURI());

		// Launch the framework
		// @formatter:off
		Framework osgiFramework = featureLauncher.launch(Files.newBufferedReader(featureJSONPath))
				.withRepository(localArtifactRepository)
				.withRepository(remoteRepository)
				.withFrameworkProperties(frameworkProperties)
				.launchFramework();
		// @formatter:on

		// Verify bundles defined in feature are installed and started
		Bundle[] bundles = osgiFramework.getBundleContext().getBundles();
		assertEquals(4, bundles.length);

		assertEquals("org.apache.felix.gogo.command", bundles[1].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[1].getState()));

		assertEquals("org.apache.felix.gogo.shell", bundles[2].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[2].getState()));

		assertEquals("org.apache.felix.gogo.runtime", bundles[3].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[3].getState()));

		// Stop framework
		osgiFramework.stop();
		osgiFramework.waitForStop(0);
	}

	@Test
	public void testLaunchFeatureWithNonFrameworkLaunchFrameworkExtension()
			throws IOException, InterruptedException, URISyntaxException, BundleException {
		// Set up a repositories
		ArtifactRepository localArtifactRepository = featureLauncher.createRepository(localM2RepositoryPath);
		assertNotNull(localArtifactRepository);

		ArtifactRepository remoteRepository = featureLauncher.createRepository(REMOTE_ARTIFACT_REPOSITORY_URI,
				Map.of(ARTIFACT_REPOSITORY_NAME, "central", LOCAL_ARTIFACT_REPOSITORY_PATH,
						localM2RepositoryPath.toString()));
		assertNotNull(remoteRepository);

		// Read Feature JSON
		Path featureJSONPath = Paths.get(getClass()
				.getResource("/features/gogo-console-launch-framework-extension-feature.non-framework.json").toURI());

		// Launch the framework
		// @formatter:off
		Framework osgiFramework = featureLauncher.launch(Files.newBufferedReader(featureJSONPath))
				.withRepository(localArtifactRepository)
				.withRepository(remoteRepository)
				.withFrameworkProperties(frameworkProperties)
				.launchFramework();
		// @formatter:on

		// Verify bundles defined in feature are installed and started
		Bundle[] bundles = osgiFramework.getBundleContext().getBundles();
		assertEquals(4, bundles.length);

		assertEquals("org.apache.felix.gogo.command", bundles[1].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[1].getState()));

		assertEquals("org.apache.felix.gogo.shell", bundles[2].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[2].getState()));

		assertEquals("org.apache.felix.gogo.runtime", bundles[3].getSymbolicName());
		assertEquals("ACTIVE", BundleStateUtil.getBundleStateString(bundles[3].getState()));

		// Stop framework
		osgiFramework.stop();
		osgiFramework.waitForStop(0);
	}
}
