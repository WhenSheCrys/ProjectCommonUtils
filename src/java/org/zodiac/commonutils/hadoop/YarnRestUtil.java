package org.zodiac.commonutils.hadoop;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class YarnRestUtil {

    private CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    private String yarnRMHost;
    private int yarnRMPort;

    public YarnRestUtil(String yarnRMHost, int yarnRMPort) {
        this.yarnRMHost = yarnRMHost;
        this.yarnRMPort = yarnRMPort;
    }


    private String getApiUrl(String action) {
        String apiPath = "ws/v1";
        return String.format("http://%s:%d/%s/%s", yarnRMHost, yarnRMPort, apiPath, action);
    }

    private String doGetAsString(String url) {
        String result = null;
        try {
            CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<YarnApplicationInfo> getApplications() {
        return getApplications(null);
    }

    public ArrayList<YarnApplicationInfo> getApplications(long top) {
        return getApplications(null, null, null, null, null, null, null, null, null, top);
    }

    public ArrayList<YarnApplicationInfo> getApplications(String user, String queue, String states, String finalStatus, String applicationTypes,
                                                          Long startedTimeBegin, Long startedTimeEnd, Long finishedTimeBegin, Long finishedTimeEnd,
                                                          Long limit) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user", user);
        map.put("queue", queue);
        map.put("states", states);
        map.put("finalStatus", finalStatus);
        map.put("applicationTypes", applicationTypes);
        map.put("startedTimeBegin", startedTimeBegin);
        map.put("startedTimeEnd", startedTimeEnd);
        map.put("finishedTimeBegin", finishedTimeBegin);
        map.put("finishedTimeEnd", finishedTimeEnd);
        map.put("limit", limit);
        return getApplications(map);
    }

    public ArrayList<YarnApplicationInfo> getApplications(Map<String, Object> config) {
        ArrayList<YarnApplicationInfo> applicationInfos = new ArrayList<>();
        StringBuilder url = new StringBuilder(getApiUrl("cluster/apps?deSelects=resouceRequests"));
        if (config != null) {
            config.forEach((k, v) -> {
                if (v != null) {
                    url.append("&").append(URLEncoder.encode(k, StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(v.toString(), StandardCharsets.UTF_8));
                }
            });
        }
        String result = doGetAsString(url.toString());
        try {
            JSONArray apps = new JSONObject(result).getJSONObject("apps").getJSONArray("app");
            for (int i = 0; i < apps.length(); i++) {
                JSONObject app = apps.getJSONObject(i);
                YarnApplicationInfo info = new YarnApplicationInfo(app.getString("id"), app.getString("user"), app.getString("name"),
                        app.getString("queue"), app.getString("state"), app.getString("finalStatus"), app.getFloat("progress"),
                        app.getString("trackingUI"), app.has("trackingUrl") ? app.getString("trackingUrl") : "",
                        app.getString("diagnostics"), app.getLong("clusterId"),
                        app.getString("applicationType"), app.getString("applicationTags"), app.getInt("priority"), app.getLong("startedTime"),
                        app.getLong("finishedTime"), app.getLong("elapsedTime"), app.has("amContainerLogs") ? app.getString("amContainerLogs") : "",
                        app.has("amHostHttpAddress") ? app.getString("amHostHttpAddress") : "",
                        app.getInt("allocatedMB"), app.getInt("allocatedVCores"), app.getInt("runningContainers"), app.getLong("memorySeconds"),
                        app.getLong("vcoreSeconds"), app.getFloat("queueUsagePercentage"), app.getFloat("clusterUsagePercentage"),
                        app.getLong("preemptedResourceMB"), app.getLong("preemptedResourceVCores"), app.getInt("numNonAMContainerPreempted"),
                        app.getInt("numAMContainerPreempted"), app.getString("logAggregationStatus"), app.getBoolean("unmanagedApplication"),
                        app.has("appNodeLabelExpression") ? app.getString("appNodeLabelExpression") : "", app.getString("amNodeLabelExpression"));
                applicationInfos.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        applicationInfos.sort(Comparator.comparing(YarnApplicationInfo::getStartedTime).reversed());
        return applicationInfos;
    }

    public boolean killApplication(String appId) {
        boolean result = false;
        String url = getApiUrl("cluster/apps/" + appId + "/" + "state");
        HttpPut httpPut = new HttpPut(url);
        String paramsStr = "{\"state\": \"KILLED\"}";
        HttpEntity params = new StringEntity(paramsStr, StandardCharsets.UTF_8);
        httpPut.setEntity(params);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPut);
            result = response.getStatusLine().getStatusCode() == 200;
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean killApplication(YarnApplicationInfo applicationInfo) {
        return killApplication(applicationInfo.getId());
    }

    public static class YarnApplicationInfo {
        private String id;
        private String user;
        private String name;
        private String queue;
        private String state;
        private String finalStatus;
        private float progress;
        private String trackingUI;
        private String trackingUrl;
        private String diagnostics;
        private long clusterId;
        private String applicationType;
        private String applicationTags;
        private int priority;
        private long startedTime;
        private long finishedTime;
        private long elapsedTime;
        private String amContainerLogs;
        private String amHostHttpAddress;
        private int allocatedMB;
        private int allocatedVCores;
        private int runningContainers;
        private long memorySeconds;
        private long vcoreSeconds;
        private float queueUsagePercentage;
        private float clusterUsagePercentage;
        private long preemptedResourceMB;
        private long preemptedResourceVCores;
        private int numNonAMContainerPreempted;
        private int numAMContainerPreempted;
        private String logAggregationStatus;
        private boolean unmanagedApplication;
        private String appNodeLabelExpression;
        private String amNodeLabelExpression;

        public YarnApplicationInfo(String id, String user, String name, String queue, String state, String finalStatus,
                                   float progress, String trackingUI, String trackingUrl, String diagnostics, long clusterId,
                                   String applicationType, String applicationTags, int priority, long startedTime,
                                   long finishedTime, long elapsedTime, String amContainerLogs, String amHostHttpAddress,
                                   int allocatedMB, int allocatedVCores, int runningContainers, long memorySeconds, long vcoreSeconds,
                                   float queueUsagePercentage, float clusterUsagePercentage, long preemptedResourceMB,
                                   long preemptedResourceVCores, int numNonAMContainerPreempted, int numAMContainerPreempted,
                                   String logAggregationStatus, boolean unmanagedApplication, String appNodeLabelExpression,
                                   String amNodeLabelExpression) {
            this.id = id;
            this.user = user;
            this.name = name;
            this.queue = queue;
            this.state = state;
            this.finalStatus = finalStatus;
            this.progress = progress;
            this.trackingUI = trackingUI;
            this.trackingUrl = trackingUrl;
            this.diagnostics = diagnostics;
            this.clusterId = clusterId;
            this.applicationType = applicationType;
            this.applicationTags = applicationTags;
            this.priority = priority;
            this.startedTime = startedTime;
            this.finishedTime = finishedTime;
            this.elapsedTime = elapsedTime;
            this.amContainerLogs = amContainerLogs;
            this.amHostHttpAddress = amHostHttpAddress;
            this.allocatedMB = allocatedMB;
            this.allocatedVCores = allocatedVCores;
            this.runningContainers = runningContainers;
            this.memorySeconds = memorySeconds;
            this.vcoreSeconds = vcoreSeconds;
            this.queueUsagePercentage = queueUsagePercentage;
            this.clusterUsagePercentage = clusterUsagePercentage;
            this.preemptedResourceMB = preemptedResourceMB;
            this.preemptedResourceVCores = preemptedResourceVCores;
            this.numNonAMContainerPreempted = numNonAMContainerPreempted;
            this.numAMContainerPreempted = numAMContainerPreempted;
            this.logAggregationStatus = logAggregationStatus;
            this.unmanagedApplication = unmanagedApplication;
            this.appNodeLabelExpression = appNodeLabelExpression;
            this.amNodeLabelExpression = amNodeLabelExpression;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getFinalStatus() {
            return finalStatus;
        }

        public void setFinalStatus(String finalStatus) {
            this.finalStatus = finalStatus;
        }

        public float getProgress() {
            return progress;
        }

        public void setProgress(float progress) {
            this.progress = progress;
        }

        public String getTrackingUI() {
            return trackingUI;
        }

        public void setTrackingUI(String trackingUI) {
            this.trackingUI = trackingUI;
        }

        public String getTrackingUrl() {
            return trackingUrl;
        }

        public void setTrackingUrl(String trackingUrl) {
            this.trackingUrl = trackingUrl;
        }

        public String getDiagnostics() {
            return diagnostics;
        }

        public void setDiagnostics(String diagnostics) {
            this.diagnostics = diagnostics;
        }

        public long getClusterId() {
            return clusterId;
        }

        public void setClusterId(long clusterId) {
            this.clusterId = clusterId;
        }

        public String getApplicationType() {
            return applicationType;
        }

        public void setApplicationType(String applicationType) {
            this.applicationType = applicationType;
        }

        public String getApplicationTags() {
            return applicationTags;
        }

        public void setApplicationTags(String applicationTags) {
            this.applicationTags = applicationTags;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public long getStartedTime() {
            return startedTime;
        }

        public void setStartedTime(long startedTime) {
            this.startedTime = startedTime;
        }

        public long getFinishedTime() {
            return finishedTime;
        }

        public void setFinishedTime(long finishedTime) {
            this.finishedTime = finishedTime;
        }

        public long getElapsedTime() {
            return elapsedTime;
        }

        public void setElapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime;
        }

        public String getAmContainerLogs() {
            return amContainerLogs;
        }

        public void setAmContainerLogs(String amContainerLogs) {
            this.amContainerLogs = amContainerLogs;
        }

        public String getAmHostHttpAddress() {
            return amHostHttpAddress;
        }

        public void setAmHostHttpAddress(String amHostHttpAddress) {
            this.amHostHttpAddress = amHostHttpAddress;
        }

        public int getAllocatedMB() {
            return allocatedMB;
        }

        public void setAllocatedMB(int allocatedMB) {
            this.allocatedMB = allocatedMB;
        }

        public int getAllocatedVCores() {
            return allocatedVCores;
        }

        public void setAllocatedVCores(int allocatedVCores) {
            this.allocatedVCores = allocatedVCores;
        }

        public int getRunningContainers() {
            return runningContainers;
        }

        public void setRunningContainers(int runningContainers) {
            this.runningContainers = runningContainers;
        }

        public long getMemorySeconds() {
            return memorySeconds;
        }

        public void setMemorySeconds(long memorySeconds) {
            this.memorySeconds = memorySeconds;
        }

        public long getVcoreSeconds() {
            return vcoreSeconds;
        }

        public void setVcoreSeconds(long vcoreSeconds) {
            this.vcoreSeconds = vcoreSeconds;
        }

        public float getQueueUsagePercentage() {
            return queueUsagePercentage;
        }

        public void setQueueUsagePercentage(float queueUsagePercentage) {
            this.queueUsagePercentage = queueUsagePercentage;
        }

        public float getClusterUsagePercentage() {
            return clusterUsagePercentage;
        }

        public void setClusterUsagePercentage(float clusterUsagePercentage) {
            this.clusterUsagePercentage = clusterUsagePercentage;
        }

        public long getPreemptedResourceMB() {
            return preemptedResourceMB;
        }

        public void setPreemptedResourceMB(long preemptedResourceMB) {
            this.preemptedResourceMB = preemptedResourceMB;
        }

        public long getPreemptedResourceVCores() {
            return preemptedResourceVCores;
        }

        public void setPreemptedResourceVCores(long preemptedResourceVCores) {
            this.preemptedResourceVCores = preemptedResourceVCores;
        }

        public int getNumNonAMContainerPreempted() {
            return numNonAMContainerPreempted;
        }

        public void setNumNonAMContainerPreempted(int numNonAMContainerPreempted) {
            this.numNonAMContainerPreempted = numNonAMContainerPreempted;
        }

        public int getNumAMContainerPreempted() {
            return numAMContainerPreempted;
        }

        public void setNumAMContainerPreempted(int numAMContainerPreempted) {
            this.numAMContainerPreempted = numAMContainerPreempted;
        }

        public String getLogAggregationStatus() {
            return logAggregationStatus;
        }

        public void setLogAggregationStatus(String logAggregationStatus) {
            this.logAggregationStatus = logAggregationStatus;
        }

        public boolean isUnmanagedApplication() {
            return unmanagedApplication;
        }

        public void setUnmanagedApplication(boolean unmanagedApplication) {
            this.unmanagedApplication = unmanagedApplication;
        }

        public String getAppNodeLabelExpression() {
            return appNodeLabelExpression;
        }

        public void setAppNodeLabelExpression(String appNodeLabelExpression) {
            this.appNodeLabelExpression = appNodeLabelExpression;
        }

        public String getAmNodeLabelExpression() {
            return amNodeLabelExpression;
        }

        public void setAmNodeLabelExpression(String amNodeLabelExpression) {
            this.amNodeLabelExpression = amNodeLabelExpression;
        }
    }

}
