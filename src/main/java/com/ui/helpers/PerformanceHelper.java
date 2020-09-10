package com.ui.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;

public class PerformanceHelper {
    private static final int REPEAT_CNT = 3;
    private static final Logger logger = LoggerFactory. getLogger(PerformanceHelper. class);

    public static long getTime(Runnable action) {
        long startTime = System.currentTimeMillis();
        try {
            action.run();
        } catch (Exception e) {
            logger.error("Unable to execute action", e);
        }
        long endTime = System.currentTimeMillis();
        return (endTime - startTime) / 1000;
    }

    public static void repeat(Runnable action, Supplier<Boolean> condition) {
        repeat(action, Collections.singletonList(condition));
    }

    public static int repeat(Runnable action, List<Supplier<Boolean>> conditions) {
        for (int i = 0; i < REPEAT_CNT; i++) {
            try {
                action.run();
            } catch (Exception e) {
                logger.error("Unable to execute action", e);
            }
            for (int j = 0; j < conditions.size(); j++) {
                if (TRUE.equals(conditions.get(j).get())) return j + 1;
            }
        }
        return 0;
    }

    public static void repeat(Runnable action, Supplier<Boolean> condition, int cnt) {
        for (int i = 0; i < cnt; i++) {
            try {
                action.run();
            } catch (Exception e) {
                logger.error("Unable to execute action", e);
            }
            if (TRUE.equals(condition.get())) break;
        }
    }

    public static void repeat(Supplier<Boolean> condition, Runnable... actions) {
        for (int i = 0; i < REPEAT_CNT; i++) {
            if (TRUE.equals(condition.get())) break;
            for (Runnable action : actions) {
                try {
                    action.run();
                } catch (Exception e) {
                    logger.error("Unable to execute action", e);
                }
            }
        }
    }

    private static <T> void ifHelper(IF<T>... conditions) {
        for (IF<T> xIf : conditions) {
            if (xIf.condition.get().equals(TRUE)) {
                xIf.action.get();
            }
        }
    }

    private static class IF<T> {
        public Supplier<T> action;
        public Supplier<Boolean> condition;

        public IF(Supplier<T> action, Supplier<Boolean> condition) {
            this.action = action;
            this.condition = condition;
        }
    }
}
