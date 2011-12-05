using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace RemotaHost
{
    public class Analyzer
    {
        // Constants
        public const int TYPE_MOUSE = 0xFFF0;
        public const int TYPE_KEYBOARD = 0xFFF1;
        public const int TYPE_UNKNOWN = 0xFFFF;
        public const int SIZE_MOUSE_DATA = 19;
        public const int SIZE_KEYBOARD_DATA = 9;
        public const int EOF = 0xC1;

        public static int getType(byte[] buffer, int bytes)
        {
            // First 2 bytes indicate the type of data.
            if (bytes <= 1)
            {
                return TYPE_UNKNOWN;
            }

            byte[] tmp = {0, 0, buffer[0], buffer[1]};
            if (BitConverter.IsLittleEndian)
            {
                Array.Reverse(tmp);
            }
            int type = BitConverter.ToInt32(tmp, 0);

            return type;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="buffer"></param>
        /// <param name="bytes"></param>
        /// <returns>MOUSEINPUT</returns>
        public static int getMouseInput(ref User32.MOUSEINPUT mi, byte[] buffer, int bytes)
        {
            // MouseInput data size is SIZE_MOUSE_DATA bytes
            if (bytes < SIZE_MOUSE_DATA || buffer.Length < SIZE_MOUSE_DATA)
            {
                throw new ArgumentOutOfRangeException();
            }

            // MouseInput data has the end of frame byte "EOF"
            if (buffer[SIZE_MOUSE_DATA-1] != EOF)
            {
                throw new ArgumentException("the end frame is not EOF");
            }

            // 0, 1: type
            if (getType(buffer, bytes) != TYPE_MOUSE)
            {
                throw new ArgumentException("not mouse event data");
            }

            // 2, 3, 4, 5: flag
            byte[] buf_flag = { buffer[2], buffer[3], buffer[4], buffer[5] };
            if (BitConverter.IsLittleEndian)
            {
                Array.Reverse(buf_flag);
            }
            int flag = BitConverter.ToInt32(buf_flag, 0);

            // 6, 7, 8, 9: x
            byte[] buf_x = { buffer[6], buffer[7], buffer[8], buffer[9] };
            if (BitConverter.IsLittleEndian)
            {
                Array.Reverse(buf_x);
            }
            int x = BitConverter.ToInt32(buf_x, 0);

            // 10, 11, 12, 13: y
            byte[] buf_y = { buffer[10], buffer[11], buffer[12], buffer[13] };
            if (BitConverter.IsLittleEndian)
            {
                Array.Reverse(buf_y);
            }
            int y = BitConverter.ToInt32(buf_y, 0);

            // 14, 15, 16, 17: whell
            byte[] buf_wheel = { buffer[14], buffer[15], buffer[16], buffer[17] };
            if (BitConverter.IsLittleEndian)
            {
                Array.Reverse(buf_wheel);
            }
            int wheel = BitConverter.ToInt32(buf_wheel, 0);

            mi.dx = x;
            mi.dy = y;
            mi.mouseData = wheel;
            mi.dwFlags = flag;

            return SIZE_MOUSE_DATA;
        }

        public static int getKeyboardInput(ref ArrayList kiList, byte[] buffer, int bytes)
        {
            User32.KEYBDINPUT ki = new User32.KEYBDINPUT();

            // KeyboardInput data size is SIZE_KEYBOARD_DATA bytes
            if (bytes < SIZE_KEYBOARD_DATA || buffer.Length < SIZE_KEYBOARD_DATA)
            {
                throw new ArgumentOutOfRangeException();
            }

            // 0, 1: type
            if (getType(buffer, bytes) != TYPE_KEYBOARD)
            {
                throw new ArgumentException("not keyboard event data");
            }

            // 2, 3: the number of inputs
            byte[] buf_nInputs = { buffer[2], buffer[3] };
            if (BitConverter.IsLittleEndian)
            {
                Array.Reverse(buf_nInputs);
            }
            int nInputs = BitConverter.ToUInt16(buf_nInputs, 0);

            // KeyboardInput data has the end of frame byte "EOF"
            if (buffer[2+2+nInputs*6+1-1] != EOF)
            {
                throw new ArgumentException("the end frame is not EOF");
            }

            int flag;
            ushort scanCode;
            for (int i = 0; i < nInputs; i++)
            {
                ki = new User32.KEYBDINPUT();

                // 4+i*6, 5+i*6, 6+i*6, 7+i*6: i-th flag
                byte[] buf_flag = { buffer[4+i*6], buffer[5+i*6], buffer[6+i*6], buffer[7+i*6] };
                if (BitConverter.IsLittleEndian)
                {
                    Array.Reverse(buf_flag);
                }
                flag = BitConverter.ToInt32(buf_flag, 0);

                // 8+i*6, 9+i*6: scan code
                byte[] buf_scanCode = { buffer[8+i*6], buffer[9+i*6] };
                if (BitConverter.IsLittleEndian)
                {
                    Array.Reverse(buf_scanCode);
                }
                scanCode = BitConverter.ToUInt16(buf_scanCode, 0);

                ki.dwFlags = flag;
                ki.wScan = scanCode;

                kiList.Add(ki);
            }

            return (2 + 2 + nInputs * 6 + 1);
        }
    }
}
