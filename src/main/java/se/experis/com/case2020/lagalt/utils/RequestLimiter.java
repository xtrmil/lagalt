package se.experis.com.case2020.lagalt.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import se.experis.com.case2020.lagalt.models.CommonResponse;

@Component
public class RequestLimiter {

    private long expireTime = msInMinutes(10);
    private int maxAttempts = 10;

    private Map<String, Queue<Long>> failedAuthentications = new HashMap<>();

    private long msInMinutes(int minutes) {
        return minutes * 60 * 1000;
    }

    public boolean addFailedAttempt(HttpServletRequest request) {
        String key = request.getRemoteAddr();
        var timestamps = failedAuthentications.get(key);
        if (timestamps == null) {
            timestamps = new LinkedList<>();
        }
        timestamps.add(System.currentTimeMillis());
        failedAuthentications.put(key, timestamps);

        return failedAuthentications.size() >= maxAttempts;
    }

    public boolean isRequestBlocked(HttpServletRequest request) {
        String key = request.getRemoteAddr();
        var timestamps = failedAuthentications.get(key);
        if (timestamps == null) {
            return false;
        }

        while (!timestamps.isEmpty() && hasExpired(timestamps.peek())) {
            timestamps.poll();
        }
        return timestamps.size() >= maxAttempts;
    }

    private boolean hasExpired(Long timestamp) {
        return timestamp + expireTime < System.currentTimeMillis();
    }

    public ResponseEntity<CommonResponse> getBlockedResponse() {
        var cr = new CommonResponse();
        cr.message = "You have made too many requests recently";
        return new ResponseEntity<>(cr, HttpStatus.TOO_MANY_REQUESTS);
    }

}
