package com.teamos.launcher;

import org.junit.Test;
import static org.junit.Assert.*;

import com.teamos.launcher.LauncherModel.AppInfo;
import com.teamos.launcher.LauncherDatabaseHelper.DbItem;

public class LauncherTest {

    @Test
    public void testAppInfoInstantiation() {
        AppInfo appInfo = new AppInfo("com.teamos.browser", "com.teamos.browser.MainActivity", "Navegador", null);
        assertEquals("com.teamos.browser", appInfo.packageName);
        assertEquals("com.teamos.browser.MainActivity", appInfo.className);
        assertEquals("Navegador", appInfo.label);
        assertNull(appInfo.icon);
    }

    @Test
    public void testDbItemInstantiation() {
        DbItem item = new DbItem(42, "com.teamos.settings", "com.teamos.settings.Settings", 0, 1, 2, 3, 1, 1);
        assertEquals(42, item.id);
        assertEquals("com.teamos.settings", item.packageName);
        assertEquals("com.teamos.settings.Settings", item.className);
        assertEquals(0, item.itemType);
        assertEquals(1, item.screen);
        assertEquals(2, item.cellX);
        assertEquals(3, item.cellY);
        assertEquals(1, item.spanX);
        assertEquals(1, item.spanY);
    }

    @Test
    public void testGridSizeCalculation() {
        int countX = 5;
        int countY = 6;
        int totalWidth = 1080;
        int totalHeight = 1920;

        int cellWidth = totalWidth / countX;
        int cellHeight = totalHeight / countY;

        assertEquals(216, cellWidth);
        assertEquals(320, cellHeight);
    }
}
