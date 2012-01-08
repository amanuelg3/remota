using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;

namespace RemotaHost
{
    public class User32
    {
        /// <summary>
        /// Constants
        /// </summary>
        public const int INPUT_MOUSE = 0x00;
        public const int INPUT_KEYBOARD = 0x01;
        public const int INPUT_HARDWARE = 0x02;

        public const int MOUSEEVENTF_MOVE = 0x0001;
        public const int MOUSEEVENTF_LEFTDOWN = 0x0002;
        public const int MOUSEEVENTF_LEFTUP = 0x0004;
        public const int MOUSEEVENTF_RIGHTDOWN = 0x0008;
        public const int MOUSEEVENTF_RIGHTUP = 0x0010;
        public const int MOUSEEVENTF_MIDDLEDOWN = 0x0020;
        public const int MOUSEEVENTF_MIDDLEUP = 0x0040;
        public const int MOUSEEVENTF_ABSOLUTE = 0x8000;
        public const int MOUSEEVENTF_WHEEL = 0x0800;

        public const uint MAPVK_VK_TO_VSC = 0x00;
        public const uint MAPVK_VSC_TO_VK = 0x01;
        public const uint MAPVK_VK_TO_CHAR = 0x02;
        public const uint MAPVK_VSC_TO_VK_EX = 0x03;
        public const uint MAPVK_VK_TO_VSC_EX = 0x04;

        [DllImport("user32.dll")]
        extern public static int SendInput(int nInputs, INPUT[] pInputs, int cbSize);

        [DllImport("user32.dll")]
        extern public static IntPtr GetMessageExtraInfo();

        [DllImport("user32.dll")]
        extern public static uint MapVirtualKey(uint uCode, uint uMapType);

        /// <summary>
        /// union INPUT
        /// </summary>
        [StructLayout(LayoutKind.Explicit)]
        public struct INPUT
        {
            [FieldOffset(0)]
            public uint type;
            [FieldOffset(4)]
            public MOUSEINPUT mi;
            [FieldOffset(4)]
            public KEYBDINPUT ki;
            [FieldOffset(4)]
            public HARDWAREINPUT hi;
        }

        /// <summary>
        /// struct MOUSEINPUT
        /// </summary>
        [StructLayout(LayoutKind.Sequential)]
        public struct MOUSEINPUT
        {
            public int dx;
            public int dy;
            public int mouseData;
            public int dwFlags;
            public int time;
            public IntPtr dwExtraInfo;
        }

        /// <summary>
        /// struct KEYBDINPUT
        /// </summary>
        [StructLayout(LayoutKind.Sequential)]
        public struct KEYBDINPUT
        {
            public ushort wVk;
            public ushort wScan;
            public int dwFlags;
            public uint time;
            public IntPtr dwExtraInfo;
        }

        /// <summary>
        /// struct HARDWAREINPUT
        /// </summary>
        [StructLayout(LayoutKind.Sequential)]
        public struct HARDWAREINPUT
        {
            public uint uMsg;
            public ushort wParamL;
            public ushort wParamH;
        }
    }
}
