package com.atakmap.android.helloworld;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import org.zeromq.SocketType;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.proto.ZPicture;

import java.util.Queue;

public class ZMQRelay extends Thread{

    public Queue<String> commandBuffer;
    ImageView image;

    String hostname = "";
    String local_ip = "127.0.0.1";
    String gcs_ip = "127.0.0.1";
    int port = 5556;
    int freq = 100;
    String mode = "tracks";
    int throttle=1500;
    int rudder=1500;



    public ZMQRelay(Queue<String> commandBuffer, ImageView cameraConnection) {
        this.commandBuffer = commandBuffer;
        image = cameraConnection;

        //hostname = socket.gethostname();
    }

    //new thread reads in data from a camera stream
    @Override
    public void run()
    {
        boolean quit= false;

        try (ZContext context = new ZContext()) {

            //loop indefinately

            while (!quit) {

                while (!commandBuffer.isEmpty()){
                    String command = commandBuffer.remove();
                    if (command.equals("exit-plugin")){
                        quit = true;
                    }
                }

                ZMQ.Socket socket = context.createSocket(SocketType.PUB);
                socket.bind("tcp://*:" + port);

                ZFrame frame = ZFrame.recvFrame(socket);
                byte[] byteArray = frame.getData();

            /*
            #General FPS and other basic infomation tracking would go here
                if (Frame_Index % 100) == 0:
                    fps = 100/(time.time()-FPS_TimeStart)
                    print("Main loop FPS:",fps)
                    FPS_TimeStart = time.time()
                    Frame_Index = 0
                Frame_Index += 1
             */

                //place frame in imageview of app
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                //ImageView image = (ImageView) findViewById(R.id.imageView1);
                image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getHeight(), false));
            }
        }


    }


    public Boolean sendMSG(String command){

        try (ZContext context = new ZContext()) {
            //toast("entered send message");
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.bind("tcp://*:" + port);

            //I HAVE ZERO BASIS FOR WHAT THESE CONTROL NUMBERS MEAN
            if (command.equals("change Mode")) {

                if (mode.equals("tracks")) {
                    mode = "screw";
                } else {
                    mode = "tracks";
                }

            }
            else if (command.equals("forward")) {
                throttle = 1500;
                rudder = 0;
            }
            else if (command.equals("back")) {
                throttle = -1500;
                rudder = 0;
            }
            else if (command.equals("turnL")) {
                throttle = 500;
                rudder = -1000;
            }
            else if (command.equals("turnR")) {
                throttle = 500;
                rudder = 1000;
            }

            String msg = String.format("%s %s %s %s", "Commands" , mode, throttle, rudder );
            socket.send(msg.getBytes(ZMQ.CHARSET), 0);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean sendJoystickMSG(MotionEvent event, int i){

        float x = event.getAxisValue(MotionEvent.AXIS_X);
        float y = event.getAxisValue(MotionEvent.AXIS_Y);

        if (i>0){
            x = event.getHistoricalAxisValue(MotionEvent.AXIS_X,i);
            y = event.getHistoricalAxisValue(MotionEvent.AXIS_Y,i);
        }

        String command = "null";
        if (x>0 && y>0){
            command = "forward";
        } else if (x<0 && y>0) {
            command = "turnL";
        } else if (x>0 && y<0) {
            command = "turnR";
        } else if (x<0 && y<0) {
            command = "back";
        }

        return sendMSG(command);
    }


}
