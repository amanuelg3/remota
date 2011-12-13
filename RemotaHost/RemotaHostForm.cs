using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;

namespace RemotaHost
{
    public partial class RemotaHostForm : Form
    {
        public RemotaHostForm()
        {
            InitializeComponent();

            foreach (String name in System.IO.Ports.SerialPort.GetPortNames())
            {
                this.serialPortListBox.Items.Add(name);
            }

            this.log(global::RemotaHost.Properties.Resources.GuideStart);
        }

        private void onStartButtonClicked(object sender, EventArgs e)
        {
            try
            {
                this.serialPort.PortName = this.serialPortListBox.SelectedItem as String;
                this.serialPort.Open();
                this.serialPort.DtrEnable = true;
                this.log(global::RemotaHost.Properties.Resources.StartSuccess);
                this.startButton.Enabled = false;
                this.stopButton.Enabled = true;
                this.serialPortListBox.Enabled = false;
            }
            catch (InvalidOperationException exception)
            {
                this.log(exception.Message);
            }
            catch (UnauthorizedAccessException exception)
            {
                this.log(exception.Message);
            }
            catch (ArgumentNullException exception)
            {
                this.log(exception.Message);
            }
        }

        private void onStopButtonClicked(object sender, EventArgs e)
        {
            try
            {
                this.serialPort.Close();
                this.log("Success");
                this.startButton.Enabled = true;
                this.stopButton.Enabled = false;
                this.serialPortListBox.Enabled = true;
                this.log(global::RemotaHost.Properties.Resources.GuideStart);
            }
            catch (InvalidOperationException exception)
            {
                this.log(exception.Message);
            }
        }

        private void onSelectedSerialPortChanged(object sender, EventArgs e)
        {
            global::RemotaHost.Properties.Settings.Default.COMPort = (string)this.serialPortListBox.SelectedItem;
            global::RemotaHost.Properties.Settings.Default.Save();
        }

        private void RemotaHostForm_Load(object sender, EventArgs e)
        {
            // Load settings
            this.moveSensitivity.Value  = global::RemotaHost.Properties.Settings.Default.MoveSensitivity;
            this.wheelSensitivity.Value = global::RemotaHost.Properties.Settings.Default.WheelSensitivity;
            this.serialPortListBox.Text = global::RemotaHost.Properties.Settings.Default.COMPort;
            this.openOnStartedCheckBox.Checked = global::RemotaHost.Properties.Settings.Default.OpenOnStarted;
        }

        private void onDataReceived(object sender, System.IO.Ports.SerialDataReceivedEventArgs e)
        {
            int bytes = this.serialPort.BytesToRead;
            int remain = bytes;
            byte[] buffer = new byte[bytes];
            this.serialPort.Read(buffer, 0, bytes);

            // If the system architecture is little-endian (that is, little end first),
            // reverse the byte array.
            if (!BitConverter.IsLittleEndian)
            {
                Array.Reverse(buffer);
            }

            Log l = new Log(this.log);
            //this.logTextBox.BeginInvoke(l, BitConverter.ToString(buffer, 0));

            while (remain != 0)
            {
                if (Analyzer.getType(buffer, bytes) == Analyzer.TYPE_MOUSE)
                {
                    User32.MOUSEINPUT mi = new User32.MOUSEINPUT();
                    try
                    {
                        int comsumption = Analyzer.getMouseInput(ref mi, buffer, bytes);
                        remain -= comsumption;
                        byte[] bufferNew = new byte[remain];
                        Array.Copy(buffer, comsumption, bufferNew, 0, remain);
                        buffer = bufferNew;

                        int defaultMoveSensitivity = global::RemotaHost.Properties.Settings.Default.MoveSensitivityDefault;
                        int defaultWheelSensitivity = global::RemotaHost.Properties.Settings.Default.WheelSensitivityDefault;
                        mi.dx = (int)(mi.dx * moveSensitivity.Value / defaultMoveSensitivity);
                        mi.dy = (int)(mi.dy * moveSensitivity.Value / defaultMoveSensitivity);
                        mi.mouseData = (int)(mi.mouseData * wheelSensitivity.Value / defaultWheelSensitivity);
                        User32.INPUT[] inputs = new User32.INPUT[1];

                        inputs[0].type = User32.INPUT_MOUSE;
                        inputs[0].mi = mi;

                        User32.SendInput(inputs.Length, inputs, Marshal.SizeOf(typeof(User32.INPUT)));
                    }
                    catch (ArgumentOutOfRangeException ae)
                    {
                        remain = 0;
                        this.logTextBox.BeginInvoke(l, ae.Message + ":" + ae.StackTrace);
                    }
                    catch (ArgumentException ae)
                    {
                        remain = 0;
                        this.logTextBox.BeginInvoke(l, ae.Message + ":" + ae.StackTrace);
                    }
                }
                else if (Analyzer.getType(buffer, bytes) == Analyzer.TYPE_KEYBOARD)
                {
                    ArrayList kiList = new ArrayList();
                    try
                    {
                        int comsumption = Analyzer.getKeyboardInput(ref kiList, buffer, bytes);
                        remain -= comsumption;
                        byte[] bufferNew = new byte[remain];
                        Array.Copy(buffer, comsumption, bufferNew, 0, remain);
                        buffer = bufferNew;

                        User32.INPUT[] inputs = new User32.INPUT[kiList.Count];
                        User32.KEYBDINPUT ki;
                        int i = 0;
                        for (IEnumerator ie = kiList.GetEnumerator(); ie.MoveNext(); )
                        {
                            ki = (User32.KEYBDINPUT)ie.Current;
                            ki.wVk = (ushort)(User32.MapVirtualKey((uint)ki.wScan, User32.MAPVK_VSC_TO_VK));

                            inputs[i].type = User32.INPUT_KEYBOARD;
                            inputs[i].ki = ki;
                            i++;
                        }

                        User32.SendInput(inputs.Length, inputs, Marshal.SizeOf(typeof(User32.INPUT)));
                    }
                    catch (ArgumentOutOfRangeException ae)
                    {
                        remain = 0;
                        this.logTextBox.BeginInvoke(l, ae.Message + ":" + ae.StackTrace);
                    }
                    catch (ArgumentException ae)
                    {
                        remain = 0;
                        this.logTextBox.BeginInvoke(l, ae.Message + ":" + ae.StackTrace);
                    }
                }
            }
            //this.logTextBox.BeginInvoke(l, System.Text.coding.Unicode.GetString(buffer));
        }

        delegate void Log(String logStr);
        private void log(String logStr)
        {
            this.logTextBox.Text += logStr + "\r\n";

            // Move the caret to the end
            this.logTextBox.SelectionStart = this.logTextBox.Text.Length;

            // Focus the textbox
            this.logTextBox.Focus();

            // Scroll the caret
            this.logTextBox.ScrollToCaret();
        }

        private void contextMenuStrip1_Opening(object sender, CancelEventArgs e)
        {

        }

        private void onSettingMenuSelected(object sender, EventArgs e)
        {
            this.Visible = true;
            if (this.WindowState == FormWindowState.Minimized)
            {
                this.WindowState = FormWindowState.Normal;
            }
        }

        private void onExitMenuSelected(object sender, EventArgs e)
        {
            notifyMainIcon.Visible = false;
            Application.Exit();
        }

        private void onMainFormClosing(object sender, FormClosingEventArgs e)
        {
            if (e.CloseReason != CloseReason.ApplicationExitCall)
            {
                e.Cancel = true;
                this.Visible = false;
            }

            global::RemotaHost.Properties.Settings.Default.Save();
        }

        private void onNotifyMainDobleClicked(object sender, EventArgs e)
        {
            this.Visible = true;
            if (this.WindowState == FormWindowState.Minimized)
            {
                this.WindowState = FormWindowState.Normal;
            }
        }

        private void onMoveSensitivityChanged(object sender, EventArgs e)
        {
            global::RemotaHost.Properties.Settings.Default.MoveSensitivity = (int)this.moveSensitivity.Value;
            global::RemotaHost.Properties.Settings.Default.Save();
        }

        private void onWheelSensitivityChanged(object sender, EventArgs e)
        {
            global::RemotaHost.Properties.Settings.Default.WheelSensitivity = (int)this.wheelSensitivity.Value;
            global::RemotaHost.Properties.Settings.Default.Save();
        }

        private void onOpenOnStartedCheckedChanged(object sender, EventArgs e)
        {
            global::RemotaHost.Properties.Settings.Default.OpenOnStarted = this.openOnStartedCheckBox.Checked;
            global::RemotaHost.Properties.Settings.Default.Save();
        }

        private void RemotaHostForm_Shown(object sender, EventArgs e)
        {
            if (this.openOnStartedCheckBox.Checked == true)
            {
                // Perform the start button
                this.startButton.PerformClick();
            }
        }
    }
}
