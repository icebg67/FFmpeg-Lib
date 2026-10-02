package net.mcreator.ffmpeglib;

import net.neoforged.fml.loading.FMLPaths;
import java.io.File;

public class FFmpegProvider {
    public static File getExecutable() {
        File d = FMLPaths.GAMEDIR.get().toFile();
        File f = new File(d, "config/ffmpeglib/ffmpeg.exe");
        if(!f.exists()) {
            System.out.println("err ffmpeg not found");
        }
        return f;
    }

    public static File getDirectory() {
        return new File(FMLPaths.GAMEDIR.get().toFile(), "config/ffmpeglib");
    }
}