package com.example.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 17:39
 */
public class Checkers {

    private PredicateHolder holderHead;
    private PredicateHolder holderTail;

    private Checkers() {
        this.holderHead = new PredicateHolder();
        this.holderTail = this.holderHead;
    }

    public static Checkers newInstance() {
        return new Checkers();
    }

    public static <T> Predicate<T> and(Predicate<? super T>... components) {
        if (components.length == 0) return null;
        Predicate predicate = components[0];
        for (int i = 1; i < components.length; i++) {
            predicate = predicate.and(components[i]);
        }
        return predicate;
    }

    public static <T> Predicate<T> or(Predicate<? super T>... components) {
        if (components.length == 0) return null;
        Predicate predicate = components[0];
        for (int i = 1; i < components.length; i++) {
            predicate = predicate.or(components[i]);
        }
        return predicate;
    }

    public static <T> Predicate<T> notNull() {
        return Objects::nonNull;
    }

    public static <T> Predicate<T> in(Collection<T> collection) {
        return input -> collection != null && collection.contains(input);
    }

    public static class StringChecker {
        public static Predicate<String> isNotBlank() {
            return StringUtils::isNotBlank;
        }
    }

    public static class FloatChecker {
        public static Predicate<Float> between(float lowerBound, float upperBound) {
            return input -> input != null && input >= lowerBound && input <= upperBound;
        }
    }

    public static class ListChecker {
        public static Predicate<List> isNotEmpty() {
            return input -> input != null && !input.isEmpty();
        }
    }

    public static class DateChecker {
        public static Predicate<String> isValidDate(String format) {
            return input -> {
                DateFormat df = new SimpleDateFormat(format);
                try {
                    df.parse(input);
                } catch (Exception e) {
                    return false;
                }
                return true;
            };
        }

        public static Predicate<Date> before(Date other) {
            return input -> input != null && other != null && input.before(other);
        }
    }


    public static class AgeChecker {
        public static Predicate<Integer> isValidAge() {
            return input -> {
                if (input != null) {
                    return input >= 0;
                }else {
                    return true;
                }
            };
        }
    }

    public static class IdentityIdChecker {
        public static Predicate<String> isValidIdentityId() {
            return input -> {
                if (input != null) {
                    return IdCardUtil.validateCard(input);
                }else {
                    return true;
                }
            };
        }
    }

    public static class GenderChecker {
        public static Predicate<Integer> isValidGender() {
            return input -> {
                if (input != null) {
                    if (input == 0 || input == 1 || input == 2) {
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    return true;
                }
            };
        }
    }

    public static class LongChecker {
        public static Predicate<Long> isValidLong() {
            return input -> {
                if (input != null) {
                    return input >= 0 ? true: false;
                }else {
                    return true;
                }
            };
        }
    }


    public <T> Checkers add(T object, Predicate<T> predicate) {
        return this.add(object, predicate, null);
    }

    public <T> Checkers add(T object, Predicate<T> predicate, String errorMsg) {
        return this.addHolder(object, predicate, errorMsg);
    }

    /**
     * check parameters
     * @return true if check parameters pass
     */
    public Result check() {
        PredicateHolder holder = holderHead;
        while (holder != null) {
            if (holder.predicate != null && !holder.predicate.test(holder.object)) {
                return new Result(holder.errorMsg);
            }
            holder = holder.next;
        }
        return new Result();
    }

    private PredicateHolder addHolder() {
        PredicateHolder holder = new PredicateHolder();
        this.holderTail = this.holderTail.next = holder;
        return holder;
    }

    private <T> Checkers addHolder(T object, Predicate predicate, String errorMsg) {
        PredicateHolder holder = this.addHolder();
        holder.object = object;
        holder.errorMsg = errorMsg;
        holder.predicate = predicate;
        return this;
    }

    private final class PredicateHolder<T> {
        T object;
        String errorMsg;
        Predicate predicate;
        PredicateHolder next;

        private PredicateHolder() {
        }
    }

    public class Result {
        boolean valid;
        String errorMsg;

        Result() {
            this.valid = true;
        }

        Result(String errorMsg) {
            this.valid = false;
            this.errorMsg = errorMsg;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }
}
