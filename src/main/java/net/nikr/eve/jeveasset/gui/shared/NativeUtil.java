/*
 * Copyright 2009-2025 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.nikr.eve.jeveasset.gui.shared;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.WinDef.HWND;
import static com.sun.jna.platform.win32.WinUser.SW_RESTORE;
import static com.sun.jna.platform.win32.WinUser.SW_SHOWMINIMIZED;
import com.sun.jna.platform.win32.WinUser.WINDOWPLACEMENT;
import com.sun.jna.win32.StdCallLibrary;
import java.io.IOException;


public class NativeUtil {

	interface User32 extends StdCallLibrary {

		//FindWindowA:         https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-findwindowa
		HWND FindWindowA(String className, String windowName);

		//ShowWindow:          https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-showwindow
		boolean ShowWindow(HWND hWnd, int command);

		//SetForegroundWindow: https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setforegroundwindow
		boolean SetForegroundWindow(HWND hWnd);

		//GetWindowPlacement:  https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getwindowplacement
		//WINDOWPLACEMENT:     https://docs.microsoft.com/en-us/windows/win32/api/winuser/ns-winuser-windowplacement
		boolean GetWindowPlacement(HWND hWnd, WINDOWPLACEMENT lpwndpl);

	}

	public static boolean focusEveOnline(String characterName) {
		final String windowName = "EVE - " + characterName;
		if (Platform.isWindows()) {
			final User32 user32 = Native.load("User32.dll", User32.class);
			HWND window = user32.FindWindowA(null, windowName);
			WINDOWPLACEMENT windowPlacement = new WINDOWPLACEMENT();
			user32.GetWindowPlacement(window, windowPlacement);
			if (windowPlacement.showCmd == SW_SHOWMINIMIZED) {
				user32.ShowWindow(window, SW_RESTORE);
			} else {
				user32.SetForegroundWindow(window);
			}
		} else if (Platform.isLinux()) {
			try {
				Runtime runtime = Runtime.getRuntime();
				String[] args = { "wmctrl", "-a", windowName};
				runtime.exec(args);
				return true;
			} catch (IOException ex) {
				return false;
			}
		} else if (Platform.isMac()) {
			try {
				Runtime runtime = Runtime.getRuntime();
				String[] args = { "osascript", "-e", "tell app \"" + windowName + "\" to activate" };
				runtime.exec(args);
				return true;
			} catch (IOException ex) {
				return false;
			}
		}
		return false;
	}
}
