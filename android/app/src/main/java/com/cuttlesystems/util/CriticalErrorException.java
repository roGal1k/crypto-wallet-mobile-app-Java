package com.cuttlesystems.util;

import java.io.IOException;

public class CriticalErrorException extends IOException {
    CriticalErrorException(String name) {
        super(name);
    }
}
