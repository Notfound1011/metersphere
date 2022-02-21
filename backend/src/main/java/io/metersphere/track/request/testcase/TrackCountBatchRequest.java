package io.metersphere.track.request.testcase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackCountBatchRequest {

    private String createUser;

    private long startTime;

    private long endTime;
}
