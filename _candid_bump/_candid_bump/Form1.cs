using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO.Ports;
using AxAXVLC;
using AXVLC;
using System.Threading;



namespace _candid_bump
{
    public partial class Form1 : Form
    {

        static SerialPort serial;
        String temp = "";

        public Form1()
        {
            InitializeComponent();
        }

       

        private void Form1_Load_1(object sender, EventArgs e)
        {
            Form form = new Form();
            AxVLCPlugin2 vlc1 = new AxVLCPlugin2();
            form.Controls.Add(vlc1);
            form.Show();


            Thread thread = new Thread(new ThreadStart(init));
            thread.Start();
            
          
        }

        public void init()
        {

            //INITIALIZE VLC
            var uri = new Uri(@"F:\videos\SEASONS\How I Met Your Mother\Season 1-5 Bloopers.FLV");
            var convertedURI = uri.AbsoluteUri;
            axVLCPlugin21.playlist.add(convertedURI);
            axVLCPlugin22.playlist.add(convertedURI);
            axVLCPlugin23.playlist.add(convertedURI);
            axVLCPlugin24.playlist.add(convertedURI);
            axVLCPlugin25.playlist.add(convertedURI);
            axVLCPlugin26.playlist.add(convertedURI);
            axVLCPlugin27.playlist.add(convertedURI);
            axVLCPlugin28.playlist.add(convertedURI); 
            
           
 
            serial = new SerialPort("COM32", 9600, Parity.None, 8, StopBits.One)
            {
                Handshake = Handshake.None
            };

            serial.DataReceived += SerialPortdataReceived;
            //serial.Open();
            
        }

        private void SerialPortdataReceived(object sender, SerialDataReceivedEventArgs e)
        {
            SerialPort sp = (SerialPort)sender;
            string tempData = sp.ReadLine();

           

            String[] indataArray = tempData.Split('@');
            
            foreach(String indata in indataArray)
            {

                if (temp != indata && indata.Length == 5)
                {
                    //NEW DATA
                    Console.Write(indata+ "  ");
                    temp = indata;

                    String[] _indata = indata.Split('_'); // 1_HIG  OR  1_LOW

                    if (_indata[1] == "HIG")
                    {
                        Console.WriteLine("Playing Screen " + _indata[0]);
                        //axVLCPlugin21.playlist.play();
                        play(returnPluginScreen(_indata[0]));
                    }
                    if (_indata[1] == "LOW")
                    {
                        Console.WriteLine("Stopping Screen " + _indata[0]);
                        //stop(returnPluginScreen(_indata[0]));
                    }
                
                } 

            }
            Array.Clear(indataArray, 0, indataArray.Length);
            Console.WriteLine("/===============================/");
             
            
            
      
        }

        public void play(AxVLCPlugin2 screen)
        {
            if( !screen.playlist.isPlaying )
                screen.playlist.play();
            

        }

       


        public void stop(AxVLCPlugin2 screen)
        {
            if (screen.playlist.isPlaying)
                screen.playlist.stop();

        }

        public AxVLCPlugin2 returnPluginScreen(String screen)
        {
            switch (screen)
            {
                case "1":
                    return axVLCPlugin21;
                case "2":
                    return axVLCPlugin22;
                case "3":
                    return axVLCPlugin23;
                case "4":
                    return axVLCPlugin24;
                case "5":
                    return axVLCPlugin25;
                case "6":
                    return axVLCPlugin26;
                case "7":
                    return axVLCPlugin27;
                case "8":
                    return axVLCPlugin28;
                default :
                    return axVLCPlugin21;
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Console.WriteLine("Play clicked");
            //INITIALIZE VLC
            axVLCPlugin21.playlist.play();
        }

       
    }
}
