package org.zodiac.commonutils.hadoop;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.impl.YarnClientImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class YarnUtil {
    private YarnClient yarnClient;

    public YarnUtil() {
        yarnClient = YarnClientImpl.createYarnClient();
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
    }

    public List<ApplicationReport> getApplications(String user, String queue, String states, String applicationTypes,
                                                   Long startedTimeBegin, Long startedTimeEnd, Long finishedTimeBegin, Long finishedTimeEnd,
                                                   Long limit) {
        List<ApplicationReport> result = new ArrayList<>();
        GetApplicationsRequest request = GetApplicationsRequest.newInstance();
        if (StringUtils.isNotEmpty(queue)) {
            request.setQueues(new HashSet<>(Arrays.asList(queue.toLowerCase().split(","))));
        }
        if (StringUtils.isNotEmpty(user)) {
            request.setUsers(new HashSet<>(Arrays.asList(user.toLowerCase().split(","))));
        }
        if (StringUtils.isNotEmpty(states)) {
            request.setApplicationStates(new HashSet<>(Arrays.asList(states.toLowerCase().split(","))));
        }
        if (StringUtils.isNotEmpty(applicationTypes)) {
            request.setApplicationTypes(new HashSet<>(Arrays.asList(applicationTypes.toLowerCase().split(","))));
        }
        if (startedTimeBegin == null) {
            startedTimeBegin = 0L;
        }
        if (startedTimeEnd == null) {
            startedTimeEnd = Long.MAX_VALUE;
        }
        request.setStartRange(Range.between(startedTimeBegin, startedTimeEnd));
        if (finishedTimeBegin == null) {
            finishedTimeBegin = 0L;
        }
        if (finishedTimeEnd == null) {
            finishedTimeEnd = Long.MAX_VALUE;
        }
        request.setFinishRange(Range.between(finishedTimeBegin, finishedTimeEnd));
        if (limit != null) {
            request.setLimit(limit);
        }
        try {
            result = yarnClient.getApplications(request);
        } catch (YarnException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<ApplicationReport> getRunningApplications() {
        return getApplications(null, null, "running", null, null, null, null, null, null);
    }

    public List<ApplicationReport> getApplications(Long limit) {
        return getApplications(null, null, null, null, null, null, null, null, limit);
    }

    public boolean killApplication(String appId) {
        boolean result = false;
        try {
            yarnClient.killApplication(ApplicationId.fromString(appId));
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean killApplication(ApplicationReport app) {
        boolean result = false;
        try {
            yarnClient.killApplication(app.getApplicationId());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
