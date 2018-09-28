package com.kimascend.light.command;

public interface CommandFactory {
    OnOffCommand onOffCommand();

    BrightnessCommand brightnessCommand();

    ColorCommand colorCommand();



}
