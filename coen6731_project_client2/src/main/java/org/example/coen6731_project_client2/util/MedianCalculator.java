package org.example.coen6731_project_client2.util;

import java.util.Collections;
import java.util.List;

public class MedianCalculator {
    public static double calculateMedian(List<Long> eachRequestTimes) {
        Collections.sort(eachRequestTimes);
        int size = eachRequestTimes.size();
        int middle = size / 2;
        if (size % 2 == 0) {
            return (eachRequestTimes.get(middle - 1) + eachRequestTimes.get(middle)) / 2.0;
        } else {
            return eachRequestTimes.get(middle);
        }
    }
}
