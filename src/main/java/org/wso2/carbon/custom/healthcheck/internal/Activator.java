/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.custom.healthcheck.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.healthcheck.api.core.HealthChecker;
import org.wso2.carbon.custom.healthcheck.WebAppHealthChecker;

public class Activator implements BundleActivator {
    private static final Log log = LogFactory.getLog(Activator.class);

    /**
     * This is called when the bundle is started.
     *
     * @param bundleContext BundleContext of this bundle
     * @throws Exception Could be thrown while bundle starting
     */
    public void start(BundleContext bundleContext) throws Exception {
        try {
            bundleContext.registerService(HealthChecker.class.getName(),
                    new WebAppHealthChecker(), null);
            log.info("Web App health monitoring service is activated..");
        } catch (Throwable e) {
            // Catching throwable to avoid retrying to initiate component.
            log.error("Failed to activate Web App health check bundle", e);
        }
    }

    /**
     * This is called when the bundle is stopped.
     *
     * @param bundleContext BundleContext of this bundle
     * @throws Exception Could be thrown while bundle stopping
     */
    public void stop(BundleContext bundleContext) throws Exception {
    }
}
