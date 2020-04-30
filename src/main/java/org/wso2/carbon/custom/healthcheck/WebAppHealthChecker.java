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
package org.wso2.carbon.custom.healthcheck;

import org.wso2.carbon.healthcheck.api.core.exception.BadHealthException;
import org.wso2.carbon.healthcheck.api.core.model.HealthCheckerConfig;
import org.wso2.carbon.healthcheck.api.core.HealthChecker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.util.Arrays;

/**
 * This class greets.
 *
 * @since 1.0.0
 */
public class WebAppHealthChecker implements HealthChecker{

    private static final Log log = LogFactory.getLog(WebAppHealthChecker.class);
    protected HealthCheckerConfig healthCheckerConfig = null;
    private static final String WEB_APP_HEALTH_CHECKER = "WebAppHealthChecker";
    private static final String MONITORED_WEB_APPS = "webapps";
    private static final String STATUS_CODE_ERROR = "error_code";
    private static final String STATUS_CODE_SUCCESS = "success_code";
    private List<String> monitoredWebApps = new ArrayList<>();
    private int success_code;
    private String error_code;

    @Override
    public String getName() {

        return WEB_APP_HEALTH_CHECKER;
    }

    @Override
    public void init(HealthCheckerConfig healthCheckerConfig) {

        this.healthCheckerConfig = healthCheckerConfig;
        initMonitoredWebApps(healthCheckerConfig);
        initStatusCode(healthCheckerConfig);
    }

    @Override
    public Properties checkHealth() throws BadHealthException {

        try {
            List<String> failedWebApps = new ArrayList<>();
            for(String webApp: monitoredWebApps){
                if (log.isDebugEnabled()) {
                    log.debug("Checking the health of the web app: " + webApp);
                }
                URL url = new URL(webApp);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                if (code != success_code) {
                    failedWebApps.add(webApp);
                    if (log.isDebugEnabled()) {
                        log.debug("Web application health check failed: " + webApp);
                    }
                }
                else{
                    if (log.isDebugEnabled()) {
                        log.debug("Web application health check passed: " + webApp);
                    }
                }
            }
            if (failedWebApps.size() != 0) {
                throw new BadHealthException(error_code,
                        "Web Applications not deployed/ not running : " + failedWebApps.toString());
            }


        } catch (Exception e) {
            // getServerStatus throws Exception.
            throw new BadHealthException(error_code,
                    "Error while getting server status", e);
        }
        return new Properties();
    }

    protected void initMonitoredWebApps(HealthCheckerConfig healthCheckerConfig) {
        if (log.isDebugEnabled()) {
            log.debug("Initializing the web apps to be monitored");
        }
        Object webappsObj = healthCheckerConfig.getProperties().get(MONITORED_WEB_APPS);
        if (webappsObj != null) {
            String[] webappsArray = webappsObj.toString().split(",");
            monitoredWebApps = Arrays.asList(webappsArray);
            if (log.isDebugEnabled()) {
                log.debug("Initialized the web apps: " + monitoredWebApps.toString());
            }
        }
    }

    protected void initStatusCode(HealthCheckerConfig healthCheckerConfig) {
        if (log.isDebugEnabled()) {
            log.debug("Initializing status codes");
        }
        Object codeObjSuccess = healthCheckerConfig.getProperties().get(STATUS_CODE_SUCCESS);
        if (codeObjSuccess != null) {
            success_code = Integer.valueOf((String) codeObjSuccess);
            if (log.isDebugEnabled()) {
                log.debug("Initialized the status code: " + success_code + " as the success response code");
            }
        }

        Object codeObjError = healthCheckerConfig.getProperties().get(STATUS_CODE_ERROR);
        if (codeObjError != null) {
            error_code = (String) codeObjError;
            if (log.isDebugEnabled()) {
                log.debug("Initialized the status code: " + error_code + " as the response code to be returned if the web application does not return the success code");
            }
        }
    }

    @Override
    public int getOrder() {

        if (this.healthCheckerConfig == null) {
            return 0;
        } else {
            return healthCheckerConfig.getOrder();
        }
    }

    @Override
    public boolean isEnabled() {

        return this.healthCheckerConfig == null || healthCheckerConfig.isEnable();
    }
}
