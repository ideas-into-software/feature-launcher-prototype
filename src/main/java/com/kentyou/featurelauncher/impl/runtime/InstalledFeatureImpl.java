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
import java.util.Objects;

import org.osgi.service.feature.ID;
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
	private final ID featureId;
	private final boolean isInitialLaunch;
	private final List<InstalledBundle> installedBundles;
	private final List<InstalledConfiguration> installedConfigurations;

	public InstalledFeatureImpl(ID featureId, boolean isInitialLaunch, List<InstalledBundle> installedBundles,
			List<InstalledConfiguration> installedConfigurations) {
		this.featureId = featureId;
		this.isInitialLaunch = isInitialLaunch;
		this.installedBundles = installedBundles;
		this.installedConfigurations = installedConfigurations;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.featurelauncher.runtime.InstalledFeature#getFeatureId()
	 */
	@Override
	public ID getFeatureId() {
		return featureId;
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
		return Objects.hash(featureId, installedBundles, installedConfigurations, isInitialLaunch);
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstalledFeatureImpl other = (InstalledFeatureImpl) obj;
		return Objects.equals(featureId, other.featureId) && Objects.equals(installedBundles, other.installedBundles)
				&& Objects.equals(installedConfigurations, other.installedConfigurations)
				&& isInitialLaunch == other.isInitialLaunch;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InstalledFeatureImpl [featureId=" + featureId + ", isInitialLaunch=" + isInitialLaunch
				+ ", installedBundles=" + installedBundles + ", installedConfigurations=" + installedConfigurations
				+ "]";
	}
}
