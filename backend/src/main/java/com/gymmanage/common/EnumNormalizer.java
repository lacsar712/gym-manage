package com.gymmanage.common;

import java.util.Locale;

public final class EnumNormalizer {
    private EnumNormalizer() {
    }

    public static String memberGender(String raw) {
        if (raw == null || raw.isBlank()) {
            return "UNKNOWN";
        }
        String v = raw.trim();
        if ("男".equals(v)) {
            return "MALE";
        }
        if ("女".equals(v)) {
            return "FEMALE";
        }
        if ("未知".equals(v)) {
            return "UNKNOWN";
        }
        v = v.toUpperCase(Locale.ROOT);
        if (!("MALE".equals(v) || "FEMALE".equals(v) || "UNKNOWN".equals(v))) {
            throw BizException.badRequest("gender must be MALE/FEMALE/UNKNOWN");
        }
        return v;
    }

    public static String activeStatus(String raw, String fieldName) {
        if (raw == null || raw.isBlank()) {
            return "ACTIVE";
        }
        String v = raw.trim().toUpperCase(Locale.ROOT);
        if (!("ACTIVE".equals(v) || "INACTIVE".equals(v))) {
            throw BizException.badRequest(fieldName + " must be ACTIVE/INACTIVE");
        }
        return v;
    }

    public static String courseStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return "OPEN";
        }
        String v = raw.trim().toUpperCase(Locale.ROOT);
        if (!("OPEN".equals(v) || "CLOSED".equals(v))) {
            throw BizException.badRequest("status must be OPEN/CLOSED");
        }
        return v;
    }

    public static String equipmentStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            throw BizException.badRequest("status is required");
        }
        String v = raw.trim().toUpperCase(Locale.ROOT);
        if (!("AVAILABLE".equals(v) || "MAINTENANCE".equals(v) || "BROKEN".equals(v))) {
            throw BizException.badRequest("status must be AVAILABLE/MAINTENANCE/BROKEN");
        }
        return v;
    }
}

