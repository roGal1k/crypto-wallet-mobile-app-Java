package com.cuttlesystems.util;

import java.io.IOException;

public class ApiMessageException extends IOException {
    ApiMessageException(String name) {
        super(name);
    }
}

