package com.awaj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.support.v4.app.Fragment;

public class GraphFragment extends Fragment {

    private float graph_height;
    private float[] audioDataTimeDomain = null;
    private float[] audioDataFreqDomain = null;

    /**
     * 0-Wave 1-Thread
     */
    private int GRAPH_VIZ_MODE = 0;
    private int GRAPH_REFRESH_DELAY = 1;
    /**
     * 0-AMP/TIME 1-AMP/FREQ
     */
    public static int GRAPH_DOMAIN_MODE;
    public static boolean TIME_DOMAIN;
    public static boolean FREQ_DOMAIN;
    //myGraphView myGraphView = new myGraphView(getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.rect);
        linearLayout.addView(new myGraphView(getActivity()));
        // Log.d("VIVZ", "Linear Layout - "+linearLayout.getHeight());
    }

    public class myGraphView extends View {

        public myGraphView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(getResources().getColor(R.color.white));

            /**Visualization Paint Object*/
            Paint graphVisualizationPO = new Paint();
            graphVisualizationPO.setColor(getResources().getColor(R.color.colorPrimaryLight));
            graphVisualizationPO.setStrokeWidth(1);

            /**Thread Paint Object*/
            Paint threadPaintObj = new Paint();
            threadPaintObj.setColor(getResources().getColor(R.color.colorPrimaryDark));
            threadPaintObj.setStrokeWidth(2);

//            if (audioDataTimeDomain == null) {
//                audioDataTimeDomain = new float[length];
//            }
            /**ACTUAL PLOTS*/
            drawMeshLines(canvas);
            if (GRAPH_DOMAIN_MODE == 1 || FREQ_DOMAIN) {
                frequencyAmplitudeGraph(canvas, graphVisualizationPO, threadPaintObj);
            } else if (GRAPH_DOMAIN_MODE == 0 || TIME_DOMAIN) {
                timeAmplitudeGraph(canvas, graphVisualizationPO, threadPaintObj);
            }
        }

        /**
         * End of onDraw Method
         */

        /**
         * Draws Mesh/Graph Lines in the background
         */
        public void drawMeshLines(Canvas canvas) {
            //Mesh Lines
            int cgh = canvas.getHeight();
            int cgw = canvas.getWidth();
            int cgh2 = cgh / 2;
            int cgw2 = cgw / 2;
            int meshDim = cgh / 30;
            int i;

            int minmdB = -90;
            int maxmdB = 0;
            /**Mesh Paint Object*/
            Paint meshObj = new Paint();
            meshObj.setColor(getResources().getColor(R.color.divider));
            meshObj.setStrokeWidth(1);

            /**Text Paint Object*/
            Paint textObj = new Paint();
            textObj.isAntiAlias();
            textObj.setColor(getResources().getColor(R.color.amber_primary_text));
            textObj.setTextSize(20);

            //Horizontal Lines - Top Segment
            for (i = cgh2; i >= 0; i -= meshDim) {
                canvas.drawLine(0, i, cgw, i, meshObj);
            }
            //Horizontal Lines - Bottom Segment
            for (i = cgh2; i <= cgh; i += meshDim) {
                canvas.drawLine(0, i, cgw, i, meshObj);
            }

            //Vertical Lines - Left Segment
            for (i = cgw2; i >= 0; i -= meshDim) {
                canvas.drawLine(i, 0, i, cgh, meshObj);
            }
            //Vertical Lines - Right Segment
            for (i = cgw2; i <= cgw; i += meshDim) {
                canvas.drawLine(i, 0, i, cgh, meshObj);
            }

            if (GRAPH_DOMAIN_MODE == 0) {
                /**AMP*/
                /**Vertical Labels*/
                int noOfVertUnits = 9;
                int dbIncrement = -90;
                int factor = cgh2 / noOfVertUnits;
                int xPos = 0;
                int yPosUp = cgh2;
                int yPosDown = cgh2;
                for (i = 0; i < noOfVertUnits; i++) {
                    /** in Y-Axis only*/
                    canvas.drawText(Integer.toString(dbIncrement), xPos, yPosUp, textObj);
                    canvas.drawText(Integer.toString(dbIncrement), xPos, yPosDown, textObj);
                    dbIncrement += 10;
                    yPosDown = yPosDown + factor;
                    yPosUp = yPosUp - factor;
                }
                /**Draw Legends for Time Domain Graph*/
                xPos = cgw - meshDim * 6;
                canvas.drawText("LEGENDS", xPos, meshDim, textObj);
                canvas.drawText("X-Axis: Time", xPos, meshDim * 2, textObj);
                canvas.drawText("Y-Axis: Amplitude (dB)", xPos, meshDim * 3, textObj);
            } else if (GRAPH_DOMAIN_MODE == 1) {
                /**Freq Labels*/

                /**BASELINE*/
                canvas.drawLine(0, cgh - meshDim, cgw, cgh - meshDim, textObj);

                /**Vertical Labels*/
                int noOfVertUnits = 9;
                int dbIncrement = -90;
                int yDecrement = cgh / noOfVertUnits;
                int xPos = 0;
                int yPos = cgh - meshDim;
                for (i = 0; i < noOfVertUnits; i++) {
                    /** in Y-Axis only*/
                    canvas.drawText(Integer.toString(dbIncrement), xPos, yPos, textObj);
                    dbIncrement += 10;
                    yPos = yPos - yDecrement;
                }

                /**Horizontal Labels*/
                int noOfHorUnits = 11;
                int minmFreq = 0;
                int maxmFreq = 8;               /**in KHz*/

                xPos = meshDim;
                int xIncrement = cgw / noOfHorUnits;
                yPos = cgh - meshDim + 20;      /**Remains constant*/

                for (i = 0; i < noOfHorUnits; i++) {
                    /** in X-Axis only*/
                    canvas.drawText(Integer.toString(minmFreq++), xPos, yPos, textObj);
                    xPos += xIncrement;
                }
                /**Draw Legends for Freq Domain Graph*/
                xPos = cgw - meshDim * 6;
                canvas.drawText("LEGENDS", xPos, meshDim, textObj);
                canvas.drawText("X-Axis: Frequency (KHz)", xPos, meshDim * 2, textObj);
                canvas.drawText("Y-Axis: Amplitude (dB)", xPos, meshDim * 3, textObj);
            }
        }

        /**
         * Time Domain Amplitude Graph
         */
        public void timeAmplitudeGraph(Canvas canvas, Paint graphVisualizationPO, Paint threadPaintObj) {
            double heightNormalizer = (canvas.getHeight() / 2) * 0.00003051757812;
            int index = 0;
            int meshDim = canvas.getHeight() / 30;

            float newX, newY;
            float oldX = meshDim, oldY = canvas.getHeight() / 2;
            float X1 = meshDim;
            float Y1 = canvas.getHeight() / 2;
            float X2, Y2;

            for (X1 = meshDim; X1 <= canvas.getWidth(); X1++) {
                try {
                    graph_height = (float) (audioDataTimeDomain[index] * heightNormalizer);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                X2 = X1;
                Y2 = Y1 - graph_height;

                /**Draws inner Wave*/
                canvas.drawLine(X1, Y1, X2, Y2, graphVisualizationPO);
                /**Draws outer Thread*/
                newX = X2;
                newY = Y2;
                canvas.drawLine(oldX, oldY, newX, newY, threadPaintObj);
                oldX = newX;
                oldY = newY;

                index++;
                postInvalidateDelayed(GRAPH_REFRESH_DELAY);
            }
            /**Highest value indicator*/
            int maxValue = (int) (FrequencyValue.findMaxValue(audioDataTimeDomain) * heightNormalizer);
            int horizontalBarHeight = (int) ((canvas.getHeight() / 2) - maxValue);
            graphVisualizationPO.setColor(Color.parseColor("#ff0000"));
            graphVisualizationPO.setStrokeWidth(2);
            canvas.drawLine(meshDim - meshDim / 10, horizontalBarHeight, canvas.getWidth(), horizontalBarHeight, graphVisualizationPO);
        }
        /**
         * Frequency Domain Amplitude Graph
         */
        public void frequencyAmplitudeGraph(Canvas canvas, Paint graphVisualizationPO, Paint threadPaintObj) {
            int index = 0;
            int meshDim = canvas.getHeight() / 30;
//            double heightNormalizer = (canvas.getHeight()-meshDim)/90;
            double heightNormalizer = 1;

            float newX, newY;
            float oldX = meshDim, oldY = canvas.getHeight() - meshDim;
            float X1 = meshDim;
            float Y1 = canvas.getHeight() - meshDim;
            float X2, Y2;

            for (X1 = meshDim; X1 <= canvas.getWidth(); X1++) {
                try {
                    graph_height = (float) (audioDataFreqDomain[index] * heightNormalizer);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                X2 = X1;
                Y2 = Y1 - graph_height;

                /**Draws inner Wave*/
                canvas.drawLine(X1, Y1, X2, Y2, graphVisualizationPO);
                /**Draws outer Thread*/
                newX = X2;
                newY = Y2;
                canvas.drawLine(oldX, oldY, newX, newY, threadPaintObj);
                oldX = newX;
                oldY = newY;

                index++;
                postInvalidateDelayed(GRAPH_REFRESH_DELAY);
            }
            /**Highest value indicator*/
            int maxValue = (int) (FrequencyValue.findMaxValue(audioDataFreqDomain) * heightNormalizer);
            int horizontalBarHeight = (int) ((canvas.getHeight() - meshDim) - maxValue);
            graphVisualizationPO.setColor(getResources().getColor(R.color.colorAccent));
            graphVisualizationPO.setStrokeWidth(2);
            canvas.drawLine(meshDim - meshDim / 10, horizontalBarHeight, canvas.getWidth(), horizontalBarHeight, graphVisualizationPO);
        }
    }

    /**
     * End of myGraphView
     */
    public void updateGraph(float[] data1, float[] data2) {
        audioDataTimeDomain = data1;
        audioDataFreqDomain = data2;
    }

    public void setMinBufferSizeInBytes(int size) {
        audioDataTimeDomain = new float[size];
    }

    public int getGraphFragmentMode() {
        return GRAPH_DOMAIN_MODE;
    }
}