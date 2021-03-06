/*
 * Copyright 2010 Phil Burk, Mobileer Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jsyn.examples;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.Function;
import com.jsyn.unitgen.FunctionOscillator;
import com.jsyn.unitgen.LineOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Play a tone using a FunctionOscillator.
 *
 * @author Phil Burk (C) 2010 Mobileer Inc
 */
public class PlayFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayFunction.class);

    private void test() {
        // Create a context for the synthesizer.
        Synthesizer synth = JSyn.createSynthesizer();

        // Start synthesizer using default stereo output at 44100 Hz.
        synth.start();

        // Add a FunctionOscillator
        FunctionOscillator oscillator = new FunctionOscillator();
        synth.add(oscillator);

        // Define a function that gives the shape of the waveform.
        Function func = input -> {
            // Input ranges from -1.0 to 1.0
            double s = Math.sin(input * Math.PI * 2.0);
            return s * s * s;
        };

        oscillator.function.set(func);

        // Add a stereo audio output unit.
        LineOut lineOut = new LineOut();
        synth.add(lineOut);

        // Connect the oscillator to both channels of the output.
        oscillator.output.connect(0, lineOut.input, 0);
        oscillator.output.connect(0, lineOut.input, 1);

        // Set the frequency and amplitude for the sine wave.
        oscillator.frequency.set(345.0);
        oscillator.amplitude.set(0.6);

        // We only need to start the LineOut. It will pull data from the
        // oscillator.
        lineOut.start();

        LOGGER.debug("You should now be hearing a sine wave. ---------");

        // Sleep while the sound is generated in the background.
        try {
            double time = synth.getCurrentTime();
            // Sleep for a few seconds.
            synth.sleepUntil(time + 4.0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.debug("Stop playing. -------------------");
        // Stop everything.
        synth.stop();
    }

    public static void main(String[] args) {
        new PlayFunction().test();
    }
}
