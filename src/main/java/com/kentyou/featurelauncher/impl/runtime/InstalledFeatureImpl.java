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
package com.kentyou.featurelauncher.impl.runtime;

import java.util.List;

import org.osgi.service.feature.Feature;
import org.osgi.service.featurelauncher.runtime.InstalledBundle;
import org.osgi.service.featurelauncher.runtime.InstalledConfiguration;
import org.osgi.service.featurelauncher.runtime.InstalledFeature;

/**
 * Implementation of {@link org.osgi.service.featurelauncher.runtime.InstalledFeature}
 * 
 * @author Michael H. Siemaszko (mhs@into.software)
 * @since Sep 15, 2024
 */
class InstalledFeatureImpl implements InstalledFeature {
	private final Feature feature;
	private final boolean isInitialLaunch;
	private final List<InstalledBundle> installedBundles;
	private final List<InstalledConfiguration> installedConfigurations;

	public InstalledFeatureImpl(Feature feature, boolean isInitialLaunch, List<InstalledBundle> installedBundles,
			List<InstalledConfiguration> installedConfigurations) {
		this.feature = feature;
		this.isInitialLaunch = isInitialLaunch;
		this.installedBundles = installedBundles;
		this.installedConfigurations = installedConfigurations;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.featurelauncher.runtime.InstalledFeature#getFeature()
	 */
	@Override
	public Feature getFeature() {
		return feature;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.featurelauncher.runtime.InstalledFeature#getOriginalFeature()
	 */
	@Override
	public Feature getOriginalFeature() {
		// TODO once decoration is implemented this will need to return
		// the original undecorated feature
		return feature;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.featurelauncher.runtime.InstalledFeature#getOriginalFeature()
	 */
	@Override
	public boolean isDecorated() {
		// TODO once decoration is implemented this will need to return
		// true if the feature has been decorated
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.featurelauncher.runtime.InstalledFeature#isInitialLaunch()
	 */
	@Override
	public boolean isInitialLaunch() {
		return isInitialLaunch;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.featurelauncher.runtime.InstalledFeature#getInstalledBundles()
	 */
	@Override
	public List<InstalledBundle> getInstalledBundles() {
		return installedBundles;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.featurelauncher.runtime.InstalledFeature#getInstalledConfigurations()
	 */
	@Override
	public List<InstalledConfiguration> getInstalledConfigurations() {
		return installedConfigurations;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// There's currently no reason to override equals and hashCode
		return super.hashCode();
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// There's currently no reason to override equals and hashCode
		return super.equals(obj);
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InstalledFeatureImpl [featureId=" + feature.getID() + ", originalFeatureId=" + feature.getID() 
				+ ", isInitialLaunch=" + isInitialLaunch + ", installedBundles=" + installedBundles
				+ ", installedConfigurations=" + installedConfigurations + "]";
	}
}
