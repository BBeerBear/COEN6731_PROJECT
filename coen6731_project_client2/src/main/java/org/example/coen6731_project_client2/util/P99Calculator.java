package org.example.coen6731_project_client2.util;

import java.util.Collections;
import java.util.List;

public class P99Calculator {
	 public static long calculateP99(List<Long> values) {
         Collections.sort(values);
         int index = (int) Math.ceil(values.size() * 0.99);
         return values.get(index - 1);
	 }
}
