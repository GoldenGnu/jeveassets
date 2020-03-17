/*
 * Copyright 2009-2020 Contributors (see credits.txt)
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
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import static com.sun.jna.platform.win32.WinUser.SW_RESTORE;
import com.sun.jna.win32.StdCallLibrary;
import java.io.IOException;
import org.jfree.chart.plot.dial.DialPointer.Pointer;


public class NativeUtil {

	interface User32 extends StdCallLibrary {
		// https://msdn.microsoft.com/en-us/library/windows/desktop/ms633499(v=vs.85).aspx

		WinDef.HWND FindWindowA(String className, String windowName);
		// https://msdn.microsoft.com/en-us/library/windows/desktop/ms633548(v=vs.85).aspx

		boolean ShowWindow(WinDef.HWND hWnd, int command);

		boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer arg);

		WinDef.HWND SetFocus(WinDef.HWND hWnd);

		int GetWindowTextA(WinDef.HWND hWnd, byte[] lpString, int nMaxCount);

		boolean SetForegroundWindow(WinDef.HWND hWnd);
	}

	public static boolean focusEveOnline(String characterName) {
		String windowName = "EVE - " + characterName;
		if (Platform.isWindows()) {
			final User32 user32 = Native.loadLibrary("User32.dll", User32.class);
			WinDef.HWND window = user32.FindWindowA(null, windowName);
			return user32.ShowWindow(window, SW_RESTORE);
		} else if (Platform.isLinux()) {
			try {
				Runtime runtime = Runtime.getRuntime();
				String[] args = { "wmctrl", "-a", windowName};
				Process process = runtime.exec(args);
				return true;
			} catch (IOException ex) {
				return false;
			}
		} else if (Platform.isMac()) {
			try {
				Runtime runtime = Runtime.getRuntime();
				String[] args = { "osascript", "-e", "tell app \"" + windowName + "\" to activate" };
				Process process = runtime.exec(args);
				return true;
			} catch (IOException ex) {
				return false;
			}
		}
		return false;
	}
}
