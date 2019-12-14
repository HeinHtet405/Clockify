package com.koekoetech.clockify.helpers;

import java.util.UUID;

public class UuidGeneratorHelper {
    public static String getGenerateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
