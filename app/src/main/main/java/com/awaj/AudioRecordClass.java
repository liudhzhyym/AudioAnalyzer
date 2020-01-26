//package com.awaj;
//
//import android.content.Context;
//import android.media.AudioRecord;
//import android.nfc.Tag;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.util.Log;
//
//import java.io.BufferedOutputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.Arrays;
//
///**
// * Created by amitgupta on 8/4/2016.
// */
//
///** Start of AudioRecordClass*/
//public class AudioRecordClass extends AsyncTask<Void,String,Void> {
//
//    StateClass stateClass = StateClass.getState();
//
//    private AudioRecordInterface listener;
//
//    final String TAG = AudioRecordClass.class.getSimpleName();
//    int minBufferSizeInBytes;
//    DatabaseHelper databaseHelper;
//    Context context = MyApplication.getAppContext();
//
//
//
//    @Override
//    protected void onPreExecute() {
//        databaseHelper = new DatabaseHelper(context);
//        databaseHelper.getAllData();
//    }
//
//    public AudioRecordClass(AudioRecordInterface listener) {
//        // set null or default listener or accept as argument to constructor
//        this.listener = listener;
//        Log.d(TAG,"constructor of AudioRecordClass");
//
//    }
//
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        Log.d("VIVZ","inside doInBAck of AudioRecordClass");
//        startRecord();
//        return null;
//    }
//
//    @Override
//    protected void onProgressUpdate(String... values ) {
//        listener.processExecuting(Float.valueOf(values[0]),Float.valueOf(values[1]),values[2]);
//    }
//
//
//    public void startRecord(){
//
//        Log.d(TAG, "Thread - Start record");
////        /**RECORDING PROCESS:
////            1.Create a file to store that data values that comes from the mic.
////            2. Fix the bufferSize and AudioRecord Object.(Will be later in detail later).
////            3.In java the data comes in the form of bytes-bytes-bytes-and so on.
////            4.In the file that we have created we can store the same byte received.
////            5.But as we have to use 16 bit PCM ENCODING SYSTEM(Quantisation), We cannot store the data in Byte form.
////            6.Thus we convert the data in short datatype and then store the array of short into the file.
////            7. short(16 bit) = 2*byte(8-bit)
////            8.And here we have used file to store the audio value from Mic and used the same file to play the Audio.
////            9.We store the data in file as Short-Short-Short(array of short) and fetch the data in same way to fetch.
////            10.But simply saying we do not needed to store and fetch from file for recording and playing for ONCE.
////            11.for that purpose , we can use the array of short datatype
////            12. Another thing is when we try to open the file via a text editor (notepad /notepad++ used by us) we cannot read
////                the actual data(short datatype) that we have store in that file.Because we have stored 16bit-16bit-16bit----
////                and most of the text editor use UTF-8 egit branchncoding which is 32-bit.
////            13.Thus to read the data we have to store it using int datatypte . int-int-int
////            14.And in this case we have to name the extension as (.txt).But when we store and fetch the data ourselves to mic and speaker
////                respectively, the extension does not matter at all . To show that I have created Three File
////                ONE- as extension Sound.pcm
////            15. AND MOST IMPORTANT THING TO REMEMBER :- OUR AMPLITUDE IS REPRESENTED BY 16 bit. SO WE USE SHORT
////         */
//        File folder = context.getExternalFilesDir("Awaj");
//        File filePcm = new File(folder,"Sound"+System.currentTimeMillis()+".pcm");
//
//        OutputStream outputStream = null;
//        BufferedOutputStream bufferedOutputStream = null;
//        DataOutputStream dataOutputStream = null;
//
//        AudioRecord audioRecord = null;
//        try {
//            filePcm.createNewFile();
//
//            // Mechanism to store fetch data from mic and store it.
//            outputStream = new FileOutputStream(filePcm);
//            bufferedOutputStream = new BufferedOutputStream(outputStream);
//            dataOutputStream = new DataOutputStream(bufferedOutputStream);
//
//            /**Call the static class of Audio Record to get the Buffer size in Byte that can handle the Audio data values based on our SAMPLING RATE (44100 hz or frame per second in our case)*/
//            //int minBufferSizeInBytes = getRecordBufferSize();//WE CAN FIX THE BUFFER SZIE BY OURSELVES
//            minBufferSizeInBytes = MainActivity.getMinBufferSizeInBytes();//FIXED THE BUFFER SZIE BY OURSELVES
//
//            // The array short that will store the Audio data that we get From the mic.
//            short[] audioData = new short[minBufferSizeInBytes];
//
//
//            //Create a Object of the AudioRecord class with the NECESSARY CONFIGURATION
//            audioRecord = new AudioRecord(,
//                    MainActivity.getSampleRateInHz(),
//                    MainActivity.getChannelsConfiguration(),
//                    MainActivity.getAudioEncoding(),
//                    minBufferSizeInBytes);
//
////            /** object of the AudioRecord class calls the startRecording() function so that every is ready and the
////             * data can be fetch from mic-buffer-our array of short(audioData)*/
//
//
//            audioRecord.startRecording();//Start Recording Based on
//
//            // it means while the user have  not pressed the RECORD-STOP Button
//            Log.d(TAG, "State:"+String.valueOf(stateClass.getRecoderingState()));
//            while(stateClass.getRecoderingState()){
//
//
////                /** numberOfShort=minBufferSize/2
////                   Actually what is happening is the minBufferSize(8 bit Buffer) is being converted to numberOfShort(16 bit buffer)
////                   AND THE MOST IMPORTANT PART IS HERE:- The actual value is being store here in the audioData array.
////                 */
//                //Writes short values into short Array and returns numberOfShort
//                int numberOfShort = audioRecord.read(audioData, 0, minBufferSizeInBytes);
//                numberOfShort = minBufferSizeInBytes/2;
//                //int numberOfShort = minBufferSizeInBytes/2;
//                int[] audioDataHalf = new int[audioData.length/2];
//                int[] audioInt = new int[audioData.length/2];
//                float[] audioFloatsForAmp = new float[audioData.length/2];
//                float[] audioFloatsForFFT= new float[audioData.length/2];
//
//                //sending audioData to graph fragment
//                //graphFragment.updateRecordGraph(audioFloatsForFFT);
//                int recordValueToGraph;
//
//
//                DecibelCalculation decibelCalculation = new DecibelCalculation();
//                for(int i = 0; i < numberOfShort; i++){
//                    //dataOutputStream.writeShort(audioData[i]); // Store in Sound.haha file as short-short-short--
//
//                    dataOutputStream.writeShort(audioData[i]);
//                    audioDataHalf[i] = audioData[i];
//                    audioInt[i]=(int)audioData[i];
//                    /**This one is for FFT*/
//                    audioFloatsForFFT[i] = (float) audioInt[i];
//                    /**This one is for Amplitude Visualization*/
//                    audioFloatsForAmp[i]=(float)audioInt[i];
//                }
//
//                float decibelValue = decibelCalculation.decibelCalculation(audioData);
//                float[] fftOutput = FftOutput.callMainFft(audioFloatsForFFT);
//
//                /**Fundamental Frequency*/
//
//                float frequency = FrequencyValue.getFundamentalFrequency(fftOutput);
//                MainActivity.plotGraph(audioFloatsForAmp,audioFloatsForFFT);
//
//
//                databaseHelper = new DatabaseHelper(MyApplication.getAppContext());
//
//                int match = databaseHelper.matchFreq(frequency);
//
//                String note = databaseHelper.getNote(match);
//
//                //Log.d("VIVZ", "note="+note+" match="+match);
//
////                if(listener!=null)
////                    listener.onDataLoaded(decibelValue,frequency,note);
//
//                publishProgress(String.valueOf(decibelValue),String.valueOf(frequency),note);
//
//
//            }
//            Log.d(TAG,"Here");
//            audioRecord.stop();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        } finally {
//            if (dataOutputStream != null) {
//                try {
//                    dataOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (bufferedOutputStream != null) {
//                try {
//                    bufferedOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (audioRecord != null) {
//                audioRecord.release();
//            }
//        }
//    }
//
//    @Override
//    protected void onPostExecute(Void Void) {
//
//        MainActivity.updateRecordState();
//        Log.d(TAG, "onPost execute stop recording");
//
//    }
//}//End of AudioRecordClass