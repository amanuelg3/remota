namespace RemotaHost
{
    partial class RemotaHostForm
    {
        /// <summary>
        /// 必要なデザイナー変数です。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 使用中のリソースをすべてクリーンアップします。
        /// </summary>
        /// <param name="disposing">マネージ リソースが破棄される場合 true、破棄されない場合は false です。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows フォーム デザイナーで生成されたコード

        /// <summary>
        /// デザイナー サポートに必要なメソッドです。このメソッドの内容を
        /// コード エディターで変更しないでください。
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(RemotaHostForm));
            this.serialPortListBox = new System.Windows.Forms.ListBox();
            this.serialPort = new System.IO.Ports.SerialPort(this.components);
            this.startButton = new System.Windows.Forms.Button();
            this.serialPortListLabel = new System.Windows.Forms.Label();
            this.logTextBox = new System.Windows.Forms.TextBox();
            this.stopButton = new System.Windows.Forms.Button();
            this.moveSensitivity = new System.Windows.Forms.NumericUpDown();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.wheelSensitivity = new System.Windows.Forms.NumericUpDown();
            this.contextMainMenuStrip = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.SettingsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.ExitToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.notifyMainIcon = new System.Windows.Forms.NotifyIcon(this.components);
            this.openOnStartedCheckBox = new System.Windows.Forms.CheckBox();
            ((System.ComponentModel.ISupportInitialize)(this.moveSensitivity)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.wheelSensitivity)).BeginInit();
            this.contextMainMenuStrip.SuspendLayout();
            this.SuspendLayout();
            // 
            // serialPortListBox
            // 
            this.serialPortListBox.FormattingEnabled = true;
            this.serialPortListBox.ItemHeight = 17;
            this.serialPortListBox.Location = new System.Drawing.Point(18, 34);
            this.serialPortListBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.serialPortListBox.Name = "serialPortListBox";
            this.serialPortListBox.Size = new System.Drawing.Size(179, 157);
            this.serialPortListBox.TabIndex = 0;
            this.serialPortListBox.SelectedIndexChanged += new System.EventHandler(this.onSelectedSerialPortChanged);
            // 
            // serialPort
            // 
            this.serialPort.ErrorReceived += new System.IO.Ports.SerialErrorReceivedEventHandler(this.onErrorReceived);
            this.serialPort.PinChanged += new System.IO.Ports.SerialPinChangedEventHandler(this.onPinChanged);
            this.serialPort.DataReceived += new System.IO.Ports.SerialDataReceivedEventHandler(this.onDataReceived);
            // 
            // startButton
            // 
            this.startButton.Location = new System.Drawing.Point(207, 34);
            this.startButton.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.startButton.Name = "startButton";
            this.startButton.Size = new System.Drawing.Size(145, 33);
            this.startButton.TabIndex = 1;
            this.startButton.Text = global::RemotaHost.Properties.Resources.Start;
            this.startButton.UseVisualStyleBackColor = true;
            this.startButton.Click += new System.EventHandler(this.onStartButtonClicked);
            // 
            // serialPortListLabel
            // 
            this.serialPortListLabel.AutoSize = true;
            this.serialPortListLabel.Location = new System.Drawing.Point(14, 12);
            this.serialPortListLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.serialPortListLabel.Name = "serialPortListLabel";
            this.serialPortListLabel.Size = new System.Drawing.Size(143, 17);
            this.serialPortListLabel.TabIndex = 2;
            this.serialPortListLabel.Text = "Select a COM port";
            // 
            // logTextBox
            // 
            this.logTextBox.Enabled = false;
            this.logTextBox.Location = new System.Drawing.Point(18, 209);
            this.logTextBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.logTextBox.Multiline = true;
            this.logTextBox.Name = "logTextBox";
            this.logTextBox.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.logTextBox.Size = new System.Drawing.Size(507, 168);
            this.logTextBox.TabIndex = 3;
            // 
            // stopButton
            // 
            this.stopButton.Enabled = false;
            this.stopButton.Location = new System.Drawing.Point(360, 34);
            this.stopButton.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.stopButton.Name = "stopButton";
            this.stopButton.Size = new System.Drawing.Size(165, 33);
            this.stopButton.TabIndex = 4;
            this.stopButton.Text = global::RemotaHost.Properties.Resources.Stop;
            this.stopButton.UseVisualStyleBackColor = true;
            this.stopButton.Click += new System.EventHandler(this.onStopButtonClicked);
            // 
            // moveSensitivity
            // 
            this.moveSensitivity.Location = new System.Drawing.Point(346, 80);
            this.moveSensitivity.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.moveSensitivity.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.moveSensitivity.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.moveSensitivity.Name = "moveSensitivity";
            this.moveSensitivity.Size = new System.Drawing.Size(180, 24);
            this.moveSensitivity.TabIndex = 5;
            this.moveSensitivity.Value = new decimal(new int[] {
            100,
            0,
            0,
            0});
            this.moveSensitivity.ValueChanged += new System.EventHandler(this.onMoveSensitivityChanged);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(208, 84);
            this.label1.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(126, 17);
            this.label1.TabIndex = 6;
            this.label1.Text = "Move sensitivity";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(208, 125);
            this.label2.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(131, 17);
            this.label2.TabIndex = 7;
            this.label2.Text = "Wheel sensitivity";
            // 
            // wheelSensitivity
            // 
            this.wheelSensitivity.Location = new System.Drawing.Point(346, 123);
            this.wheelSensitivity.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.wheelSensitivity.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.wheelSensitivity.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.wheelSensitivity.Name = "wheelSensitivity";
            this.wheelSensitivity.Size = new System.Drawing.Size(180, 24);
            this.wheelSensitivity.TabIndex = 8;
            this.wheelSensitivity.Value = new decimal(new int[] {
            100,
            0,
            0,
            0});
            this.wheelSensitivity.ValueChanged += new System.EventHandler(this.onWheelSensitivityChanged);
            // 
            // contextMainMenuStrip
            // 
            this.contextMainMenuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.SettingsToolStripMenuItem,
            this.ExitToolStripMenuItem});
            this.contextMainMenuStrip.Name = "contextMainMenuStrip";
            this.contextMainMenuStrip.Size = new System.Drawing.Size(141, 60);
            this.contextMainMenuStrip.Text = "RemotaHost";
            this.contextMainMenuStrip.Opening += new System.ComponentModel.CancelEventHandler(this.contextMenuStrip1_Opening);
            // 
            // SettingsToolStripMenuItem
            // 
            this.SettingsToolStripMenuItem.Name = "SettingsToolStripMenuItem";
            this.SettingsToolStripMenuItem.Size = new System.Drawing.Size(140, 28);
            this.SettingsToolStripMenuItem.Text = global::RemotaHost.Properties.Resources.Settings;
            this.SettingsToolStripMenuItem.Click += new System.EventHandler(this.onSettingMenuSelected);
            // 
            // ExitToolStripMenuItem
            // 
            this.ExitToolStripMenuItem.Name = "ExitToolStripMenuItem";
            this.ExitToolStripMenuItem.Size = new System.Drawing.Size(140, 28);
            this.ExitToolStripMenuItem.Text = global::RemotaHost.Properties.Resources.Exit;
            this.ExitToolStripMenuItem.Click += new System.EventHandler(this.onExitMenuSelected);
            // 
            // notifyMainIcon
            // 
            this.notifyMainIcon.ContextMenuStrip = this.contextMainMenuStrip;
            this.notifyMainIcon.Icon = ((System.Drawing.Icon)(resources.GetObject("notifyMainIcon.Icon")));
            this.notifyMainIcon.Text = global::RemotaHost.Properties.Resources.AppName;
            this.notifyMainIcon.Visible = true;
            this.notifyMainIcon.DoubleClick += new System.EventHandler(this.onNotifyMainDobleClicked);
            // 
            // openOnStartedCheckBox
            // 
            this.openOnStartedCheckBox.AutoSize = true;
            this.openOnStartedCheckBox.Location = new System.Drawing.Point(211, 165);
            this.openOnStartedCheckBox.Name = "openOnStartedCheckBox";
            this.openOnStartedCheckBox.Size = new System.Drawing.Size(167, 21);
            this.openOnStartedCheckBox.TabIndex = 10;
            this.openOnStartedCheckBox.Text = global::RemotaHost.Properties.Resources.OpenOnStarted;
            this.openOnStartedCheckBox.UseVisualStyleBackColor = true;
            this.openOnStartedCheckBox.CheckedChanged += new System.EventHandler(this.onOpenOnStartedCheckedChanged);
            // 
            // RemotaHostForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 17F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.SystemColors.Control;
            this.ClientSize = new System.Drawing.Size(545, 390);
            this.Controls.Add(this.openOnStartedCheckBox);
            this.Controls.Add(this.wheelSensitivity);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.moveSensitivity);
            this.Controls.Add(this.serialPortListLabel);
            this.Controls.Add(this.stopButton);
            this.Controls.Add(this.logTextBox);
            this.Controls.Add(this.startButton);
            this.Controls.Add(this.serialPortListBox);
            this.Font = new System.Drawing.Font("MS UI Gothic", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(128)));
            this.ForeColor = System.Drawing.SystemColors.ControlText;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Name = "RemotaHostForm";
            this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Show;
            this.Text = "RemotaHost";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.onMainFormClosing);
            this.Load += new System.EventHandler(this.RemotaHostForm_Load);
            this.Shown += new System.EventHandler(this.RemotaHostForm_Shown);
            ((System.ComponentModel.ISupportInitialize)(this.moveSensitivity)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.wheelSensitivity)).EndInit();
            this.contextMainMenuStrip.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ListBox serialPortListBox;
        private System.IO.Ports.SerialPort serialPort;
        private System.Windows.Forms.Button startButton;
        private System.Windows.Forms.Label serialPortListLabel;
        private System.Windows.Forms.TextBox logTextBox;
        private System.Windows.Forms.Button stopButton;
        private System.Windows.Forms.NumericUpDown moveSensitivity;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.NumericUpDown wheelSensitivity;
        private System.Windows.Forms.ContextMenuStrip contextMainMenuStrip;
        private System.Windows.Forms.NotifyIcon notifyMainIcon;
        private System.Windows.Forms.ToolStripMenuItem SettingsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem ExitToolStripMenuItem;
        private System.Windows.Forms.CheckBox openOnStartedCheckBox;

    }
}

