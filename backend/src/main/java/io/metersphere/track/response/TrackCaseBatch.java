package io.metersphere.track.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 全量用例数据统计
 */
@Getter
@Setter
public class TrackCaseBatch {

    /**
     * 用例总数
     */
    private long allCaseCountNumber = 0;

    /**
     * 每个用户的用例总数
     */
    private long allCaseCountNumberByUser = 0;

    /**
     * 用例统计
     */
    private List<TrackCountResult> caseCount ;
}
