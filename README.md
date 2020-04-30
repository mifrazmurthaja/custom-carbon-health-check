# custom-carbon-health-check
This is a sample custom carbon health checker for WSO2 carbon as defined at [Monitoring Server Health Administration Guide](https://docs.wso2.com/display/ADMIN44x/Monitoring+Server+Health) which is implemented using the HealthChecker API available at [WSO2 carbon-health-check repo](https://github.com/wso2/carbon-health-check) to test the health of any web application endpoints.

The current implementation is supported for WSO2 IS 5.9.0 and you may change the project version in [pom.xml](pom.xml#L24) to support other versions  based on the ```org.wso2.carbon.healthcheck.api.core``` version available in the product accordingly.
For example,
- IS 5.7.0 : 1.0.0
- IS 5.8.0 : 1.0.3
- IS 5.9.0 : 1.2.1

### Deploying the API
- Do the necessary modifications to the project based on your requirement and build the project using ```mvn clean install```
- Copy the **org.wso2.carbon.healthcheck-1.x.x.jar** available in the target directory to <IS_HOME>/repository/components/dropins directory of WSO2 IS Server.

### Configuring the API
1. Set Enable to true for CarbonHealthCheckConfigs in **health-check-config.xml** file resides in <IS_HOME>/repository/conf directory to enable the Carbon Health Check API as below.
    ```
    <CarbonHealthCheckConfigs>
      <Enable>true</Enable>
      <HealthCheckers>

      </HealthCheckers>
    </CarbonHealthCheckConfigs>
    ```
2. Enable the custom carbon health checker and define the properties as below. (You may enable/ disable the default health checkers based on your requirement)
    ```
   <HealthCheckers> 
        <HealthChecker name="WebAppHealthChecker" orderId="90" enable="true">
          <Property name="success_code">200</Property>
          <Property name="error_code">HC_00006</Property>
          <Property name="webapps">https://localhost:9443/carbon/product/about.html, https://localhost:9443/webapp1</Property>
        </HealthChecker>
   <HealthCheckers>
    ```
The properties used in the custom carbon health check API are explained below.
- success_code  : The return code to be returned to identify if the web applications are up and running.
- error_code    : The error code to be returned if any of the web applications are not deployed/ not running when the carbon health check API is invoked.
- webapps       : The web applications to be checked when the carbon health check API is invoked.

You may refer [Monitoring Server Health Administration Guide](https://docs.wso2.com/display/ADMIN44x/Monitoring+Server+Health) for further details on configuring the Carbon Health Check API.

If you want to configure the custom carbon health checker for WSO2 IS 5.9.0, you may configure the same using the deployment.toml as below.
```
[carbon_health_check]
enable = true

[[health_checker]]
name = "WebAppHealthChecker"
order = "90"

[health_checker.properties]
success_code = 200
error_code = "HC_00006"
webapps = "https://localhost:9443/carbon/product/about.html, https://localhost:9443/webapp1"
```
Please refer [Monitoring Server Health - 5.9.0 Doc](https://is.docs.wso2.com/en/5.9.0/setup/monitoring-server-health/#adding-new-health-checkers) for more details on configuring the Carbon Health Check API for WSO2 IS 5.9.0.

### Invoking the API
This is an open API that should ideally be blocked at the load balancer level. To invoke it, start the WSO2 product and send a GET request to the health check API. A sample cURL command is shown below.
```
curl -k -v https://{hostname}:{port}/api/health-check/v1.0/health
```
The code block below shows a sample 503 Unavailable response with an array of errors. Please note that the code: HC_00006 is the error code we've configured above.
```
{
   "errors":[
      {
         "code":"HC_00006",
         "message":"Error while getting server status",
         "description":"Web Applications not deployed/ not running : [ https://localhost:9443/webapp1]"
      }
   ]
}
```
This will return 200 OK response if all the web applications are up and running.
