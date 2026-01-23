package com.cloud_idaas.core.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class BrowserUtil {

    public static void open(URI uri) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(uri);
    }
}
